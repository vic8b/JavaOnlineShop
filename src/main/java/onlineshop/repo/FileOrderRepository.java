package onlineshop.repo;

import lombok.NonNull;
import onlineshop.domain.order.Order;
import onlineshop.domain.order.OrderItem;
import onlineshop.domain.order.OrderStatus;
import onlineshop.domain.product.Computer;
import onlineshop.domain.product.Electronics;
import onlineshop.domain.product.Product;
import onlineshop.domain.product.Smartphone;
import onlineshop.domain.useraccount.Account;
import onlineshop.exception.OrderAlreadyExistsException;
import onlineshop.exception.OrderPersistenceException;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileOrderRepository implements OrderRepository {
    private final Map<UUID, Order> orderRepo = new ConcurrentHashMap<>();
    private final Path file;

    public FileOrderRepository(@NonNull Path file) {
        this.file = file;
        initializeFile();
        loadOrders();
    }

    @Override
    public synchronized void add(@NonNull Order order) {
        if (orderRepo.containsKey(order.getOrderId())) {
            throw new OrderAlreadyExistsException(order.getOrderId().toString());
        }

        Map<UUID, Order> updatedOrders = new HashMap<>(orderRepo);
        updatedOrders.put(order.getOrderId(), order);

        saveAll(updatedOrders);

        orderRepo.put(order.getOrderId(), order);

        System.out.println("Order: " + order.getOrderId() + " has been added to the repository");
    }

    @Override
    public Optional<Order> findById(@NonNull UUID orderId) {
        return Optional.ofNullable(orderRepo.get(orderId));
    }

    @Override
    public List<Order> findAll() {
        return List.copyOf(orderRepo.values());
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
                    .map(this::deserializeOrder)
                    .forEach(order -> {
                        Order previous = orderRepo.putIfAbsent(order.getOrderId(), order);
                        if (previous != null) {
                            throw new OrderPersistenceException("Duplicate order id in file: " + order.getOrderId());
                        }
                    });
        } catch (IOException e) {
            throw new OrderPersistenceException("Could not load orders", e);
        }
    }

    private void saveAll(Map<UUID, Order> ordersToSave) {
        List<String> lines = ordersToSave.values().stream()
                .map(this::serializeOrder)
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

    private String serializeOrder(Order order) {
        String serializedItems = order.getItems().stream()
                .map(this::serializeOrderItem)
                .collect(Collectors.joining(";"));

        return String.join(
                "|",
                order.getOrderId().toString(),
                order.getOrderDate().toString(),
                order.getOrderStatus().name(),
                order.getTotalPrice().toPlainString(),
                order.getAccount().getAccountId(),
                order.getAccount().getFirstName(),
                order.getAccount().getLastName(),
                order.getAccount().getEmail(),
                serializedItems
        );
    }

    private String serializeOrderItem(OrderItem orderItem) {
        Product product = orderItem.getProduct();

        ArrayList<String> values = new ArrayList<>();

        values.add(productType(product));
        values.add(product.getId());
        values.add(product.getName());
        values.add(product.getPrice().toPlainString());
        values.add(String.valueOf(product.getQuantity()));
        values.add(String.valueOf(orderItem.getQuantity()));
        values.add(orderItem.getUnitPrice().toPlainString());

        if (product instanceof Computer computer) {
            values.add(computer.getCpu());
            values.add(computer.getRam());
        } else if (product instanceof Smartphone smartphone) {
            values.add(smartphone.getColor());
            values.add(smartphone.getBatteryCapacity());

            String accessories = String.join("~", smartphone.getAccessories());

            values.add(accessories);
        }

        return String.join(",", values);
    }

    private String productType(Product product) {
        if (product instanceof Computer) {
            return "COMPUTER";
        }

        if (product instanceof Smartphone) {
            return "SMARTPHONE";
        }

        if (product instanceof Electronics) {
            return "ELECTRONICS";
        }

        throw new OrderPersistenceException("Unsupported product type: " + product.getClass().getName());
    }

    private Order deserializeOrder(String orderLine) {
        try {
            String[] parts = orderLine.split("\\|", -1);

            if (parts.length != 9) {
                throw new OrderPersistenceException("Invalid order record: " + orderLine);
            }

            UUID orderId = UUID.fromString(parts[0]);
            Instant orderDate = Instant.parse(parts[1]);
            OrderStatus orderStatus = OrderStatus.valueOf(parts[2]);
            BigDecimal totalPrice = new BigDecimal(parts[3]);

            Account account = Account.builder()
                    .accountId(parts[4])
                    .firstName(parts[5])
                    .lastName(parts[6])
                    .email(parts[7])
                    .build();

            List<OrderItem> items = deserializeOrderItems(parts[8]);

            return new Order(
                    orderId,
                    account,
                    items,
                    totalPrice,
                    orderDate,
                    orderStatus
            );
        } catch (OrderPersistenceException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new OrderPersistenceException("Could not deserialize order: " + orderLine, e);
        }
    }

    private List<OrderItem> deserializeOrderItems(String serializedItems) {
        if (serializedItems.isBlank()) {
            return List.of();
        }
        
        return Arrays.stream(serializedItems.split(";"))
                .map(this::deserializeOrderItem)
                .toList();
    }

    private OrderItem deserializeOrderItem(String value) {
        String[] parts = value.split(",", -1);

        String productType = parts[0];
        String productId = parts[1];
        String productName = parts[2];
        BigDecimal productPrice = new BigDecimal(parts[3]);
        int productQuantity = Integer.parseInt(parts[4]);
        int orderedQuantity = Integer.parseInt(parts[5]);
        BigDecimal unitPrice = new BigDecimal(parts[6]);

        Product product = switch (productType) {
            case "COMPUTER" -> deserializeComputer(
                    parts,
                    productId,
                    productName,
                    productPrice,
                    productQuantity
            );

            case "SMARTPHONE" -> deserializeSmartphone(
                    parts,
                    productId,
                    productName,
                    productPrice,
                    productQuantity
            );

            case "ELECTRONICS" -> Electronics.builder()
                    .id(productId)
                    .name(productName)
                    .price(productPrice)
                    .quantity(productQuantity)
                    .build();

            default -> throw new OrderPersistenceException("Unknown product type: " + productType);
        };

        return OrderItem.builder()
                .product(product)
                .quantity(orderedQuantity)
                .unitPrice(unitPrice)
                .build();
    }

    private Computer deserializeComputer(
            String[] parts,
            String id,
            String name,
            BigDecimal price,
            int quantity
    ) {
        if (parts.length != 9) {
            throw new OrderPersistenceException(
                    "Invalid computer order item"
            );
        }

        return Computer.builder()
                .id(id)
                .name(name)
                .price(price)
                .quantity(quantity)
                .cpu(parts[7])
                .ram(parts[8])
                .build();
    }

    private Smartphone deserializeSmartphone(
            String[] parts,
            String id,
            String name,
            BigDecimal price,
            int quantity
    ) {
        if (parts.length != 10) {
            throw new OrderPersistenceException(
                    "Invalid smartphone order item"
            );
        }

        List<String> accessories;

        if (parts[9].isBlank()) {
            accessories = List.of();
        } else {
            accessories = Arrays.stream(parts[9].split("~"))
                    .toList();
        }

        Smartphone.SmartphoneBuilder builder = Smartphone.builder()
                .id(id)
                .name(name)
                .price(price)
                .quantity(quantity)
                .color(parts[7])
                .batteryCapacity(parts[8]);

        accessories.forEach(builder::accessory);

        return builder.build();
    }
}
