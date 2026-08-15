package onlineshop.service.invoice;

import onlineshop.domain.invoice.Invoice;
import onlineshop.domain.order.Order;
import onlineshop.domain.useraccount.Account;
import onlineshop.repo.InvoiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceQueryServiceTest {
    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private Invoice firstInvoice;

    @Mock
    private Invoice secondInvoice;

    @Mock
    private Order firstOrder;

    @Mock
    private Order secondOrder;

    @Mock
    private Account firstAccount;

    @Mock
    private Account secondAccount;

    @Test
    void shouldFindInvoicesForAccount() {
        when(firstInvoice.getOrder()).thenReturn(firstOrder);
        when(secondInvoice.getOrder()).thenReturn(secondOrder);

        when(firstOrder.getAccount()).thenReturn(firstAccount);
        when(secondOrder.getAccount()).thenReturn(secondAccount);

        when(firstAccount.getAccountId()).thenReturn("ACC-1");
        when(secondAccount.getAccountId()).thenReturn("ACC-2");

        when(invoiceRepository.findAll())
                .thenReturn(List.of(firstInvoice, secondInvoice));

        InvoiceQueryService invoiceQueryService = new InvoiceQueryService(invoiceRepository);

        List<Invoice> invoicesForAccount = invoiceQueryService.findInvoicesForAccount("ACC-1");

        assertThat(invoicesForAccount).containsExactly(firstInvoice);
    }

    @Test
    void shouldReturnEmptyListWhenAccountHasNoInvoices() {
        when(invoiceRepository.findAll()).thenReturn(List.of());

        InvoiceQueryService invoiceQueryService = new InvoiceQueryService(invoiceRepository);

        assertThat(invoiceQueryService.findInvoicesForAccount("ACC-1")).isEmpty();
    }
}
