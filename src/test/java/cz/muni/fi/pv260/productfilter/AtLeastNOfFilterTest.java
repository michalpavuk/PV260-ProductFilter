package cz.muni.fi.pv260.productfilter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtLeastNOfFilterTest {

    @Mock
    private Filter<Object> filter1;

    @Mock
    private Filter<Object> filter2;

    private Object object = new Object();

    @Test
    void constructorThrowsErrors() {
        Assertions.assertThrows(FilterNeverSucceeds.class, () -> {
            new AtLeastNOfFilter(2, filter1);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new AtLeastNOfFilter(0, filter1);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new AtLeastNOfFilter(0);
        });
    }

    @Test
    void filterPassesIfChildrenPass() {
        AtLeastNOfFilter<Object> atLeastNOfFilter = new AtLeastNOfFilter<>(2, filter1, filter2);

        when(filter1.passes(object)).thenReturn(true);
        when(filter2.passes(object)).thenReturn(true);

        Assertions.assertTrue(atLeastNOfFilter.passes(object));
    }

    @Test
    void oneOfTwoFilterPassesParentPassesToo() {
        AtLeastNOfFilter<Object> atLeastNOfFilter = new AtLeastNOfFilter<>(1, filter1, filter2);

        when(filter1.passes(object)).thenReturn(false);
        when(filter2.passes(object)).thenReturn(true);

        Assertions.assertTrue(atLeastNOfFilter.passes(object));
    }

    @Test
    void filterFailsIfNIsNotMet() {
        AtLeastNOfFilter<Object> atLeastNOfFilter = new AtLeastNOfFilter<>(2, filter1, filter2);

        when(filter1.passes(object)).thenReturn(false);
        when(filter2.passes(object)).thenReturn(false);

        Assertions.assertFalse(atLeastNOfFilter.passes(object));
    }
}