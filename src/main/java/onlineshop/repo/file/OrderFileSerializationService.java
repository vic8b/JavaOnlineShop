package onlineshop.repo.file;

import onlineshop.domain.order.Order;
import onlineshop.domain.order.OrderItem;
import onlineshop.domain.order.OrderStatus;
import onlineshop.domain.product.Computer;
import onlineshop.domain.product.Electronics;
import onlineshop.domain.product.Product;
import onlineshop.domain.product.Smartphone;
import onlineshop.domain.useraccount.Account;
import onlineshop.domain.useraccount.Email;
import onlineshop.exception.OrderPersistenceException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderFileSerializationService {
    public String encode(Order order) {
        return serializeOrder(order);
    }

    public Order decode(String line) {
        return deserializeOrder(line);
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
                order.getAccount().getEmail().value(),
                serializedItems
        );
    }

    private String serializeOrderItem(OrderItem orderItem) {
        Product product = orderItem.product();

        ArrayList<String> values = new ArrayList<>();

        values.add(productType(product));
        values.add(product.getId());
        values.add(product.getName());
        values.add(product.getPrice().toPlainString());
        values.add(String.valueOf(product.getQuantity()));
        values.add(String.valueOf(orderItem.quantity()));
        values.add(orderItem.unitPrice().toPlainString());

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
                    .email(new Email(parts[7]))
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
