package cz.muni.fi.pv260.productfilter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedList;

import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ControllerTest {

    @Mock
    private Input input;

    @Mock
    private Output output;

    @Mock
    private Logger logger;

    private Collection<Product> collection = new LinkedList<>();

    @InjectMocks
    private Controller controller;

    @BeforeEach
    void before() throws ObtainFailedException {
        MockitoAnnotations.initMocks(this);

        collection.add(new Product(1, "first", Color.BLACK, new BigDecimal(1)));
        collection.add(new Product(2, "second", Color.BLACK, new BigDecimal(2)));
        collection.add(new Product(3, "third", Color.RED, new BigDecimal(10)));
        collection.add(new Product(4, "fourth", Color.RED, new BigDecimal(20)));

        when(input.obtainProducts()).thenReturn(collection);
    }

    @Test
    void filteredProductsAreSentToOutput() {
        ColorFilter filter = new ColorFilter(Color.BLACK);

        controller.select(filter);

        Collection<Product> expected = new LinkedList<>();
        expected.add((Product) collection.toArray()[0]);
        expected.add((Product) collection.toArray()[1]);

        verify(output).postSelectedProducts(expected);
    }

    @Test
    void controllerLogsTheMessageOnSuccess() {
        ColorFilter filter = new ColorFilter(Color.BLACK);

        controller.select(filter);

        verify(logger).setLevel("INFO");
        verify(logger).log(Controller.TAG_CONTROLLER,"Successfully selected 2 out of 4 available products.");
    }

    @Test
    void controllerLogsException() throws ObtainFailedException {
        ColorFilter filter = new ColorFilter(Color.BLACK);
        Exception exception = new ObtainFailedException();
        when(input.obtainProducts()).thenThrow(exception);

        controller.select(filter);

        verify(logger).setLevel("ERROR");
        verify(logger).log(Controller.TAG_CONTROLLER, "Filter procedure failed with exception: " + exception);
    }

    @Test
    void outputIsEmptyOnException() throws ObtainFailedException {
        ColorFilter filter = new ColorFilter(Color.BLACK);
        Exception exception = new ObtainFailedException();
        when(input.obtainProducts()).thenThrow(exception);

        controller.select(filter);

        verify(output, never()).postSelectedProducts(any());
    }
}