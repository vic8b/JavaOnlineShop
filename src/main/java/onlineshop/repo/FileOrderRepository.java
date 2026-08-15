package onlineshop.repo;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import onlineshop.domain.order.Order;
import onlineshop.exception.OrderAlreadyExistsException;
import onlineshop.exception.OrderPersistenceException;
import onlineshop.repo.file.OrderFileSerializationService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Slf4j
public class FileOrderRepository implements OrderRepository {
    private final Map<UUID, Order> orderRepo = new ConcurrentHashMap<>();
    private final Path file;
    private final OrderFileSerializationService orderFileSerializationService = new OrderFileSerializationService();

    public FileOrderRepository(@NonNull Path file) {
        this.file = file;
        initializeFile();
        loadOrders();
    }

    @Override
    public synchronized void add(@NonNull Order order) {
        validateIfOrderAlreadyExists(order);

        Map<UUID, Order> updatedOrders = new HashMap<>(orderRepo);
        updatedOrders.put(order.getOrderId(), order);

        saveAll(updatedOrders);

        orderRepo.put(order.getOrderId(), order);

        log.info("Order: {} has been added to the repository", order.getOrderId());
    }

    @Override
    public Optional<Order> findById(@NonNull UUID orderId) {
        return Optional.ofNullable(orderRepo.get(orderId));
    }

    @Override
    public List<Order> findAll() {
        return List.copyOf(orderRepo.values());
    }

    private void validateIfOrderAlreadyExists(Order order) {
        if (orderRepo.containsKey(order.getOrderId())) {
            throw new OrderAlreadyExistsException(order.getOrderId().toString());
        }
    }

    private void initializeFile() {
        try {
            Path parent = file.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            if (Files.exists(file) && Files.isDirectory(file)) {
                throw new OrderPersistenceException("Expected a file, but path is directory");
            }

            if (Files.notExists(file)) {
                Files.createFile(file);
            }
        } catch (IOException e) {
            throw new OrderPersistenceException("Could not initialize orders file", e);
        }
    }

    private void loadOrders() {
        try (Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
            lines.filter(line -> !line.isBlank())
                    .map(orderFileSerializationService::decode)
                    .forEach(this::addLoadedOrder);
        } catch (IOException e) {
            throw new OrderPersistenceException("Could not load orders", e);
        }
    }

    private void addLoadedOrder(Order order) {
        Order previous = orderRepo.putIfAbsent(order.getOrderId(), order);
        if (previous != null) {
            throw new OrderPersistenceException("Duplicate order id in file: " + order.getOrderId());
        }
    }

    private void saveAll(Map<UUID, Order> ordersToSave) {
        List<String> lines = ordersToSave.values().stream()
                .map(orderFileSerializationService::encode)
                .toList();

        try {
            Files.write(
                    file,
                    lines,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            throw new OrderPersistenceException("Could not save orders to file: " + file, e);
        }
    }
}
