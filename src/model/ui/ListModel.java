package model.ui;

import java.util.ArrayList;
import java.util.List;
import util.Logger;

import org.jooq.lambda.Unchecked;

import application.Services;
import javafx.application.Platform;
import model.Model;

/**
 * The Class ListModel.
 *
 * @param <T> the generic type
 */
public class ListModel<T> extends Model {

    /** The logger. */
    // a logger for good measure
    private static Logger logger = Logger.getLogger();

    /** The data. */
    private final List<T> data;

    /**
     * Instantiates a new list model.
     *
     * @param name the name
     */
    public ListModel(String name) {
        super(name);
        this.data = new ArrayList<T>();
    }

    /* (non-Javadoc)
     * @see models.Model#addData(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void addData(Object data) {
        Platform.runLater(
            Unchecked.runnable(
                () -> this.data.add((T) data),
                exception -> ListModel.logger.warning("Model '" + this.getName() + "' failed to add data.")));
    }

    /* (non-Javadoc)
     * @see models.Model#getData()
     */
    @Override
    public List<T> getData() {
        return this.data;
    }

    /* (non-Javadoc)
     * @see models.Model#setData(java.lang.Object)
     */
    @Override
    public void setData(Object data) {
        // TODO Auto-generated method stub

    }

}
