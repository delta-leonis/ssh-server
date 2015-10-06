package services.producers;

import services.Producer;

/**
 * The Class IntProducer.
 */
public class IntProducer extends Producer {

    /**
     * Instantiates a new int producer.
     *
     * @param name the name
     */
    public IntProducer (String name) {
        super(name);
        this.setCallable(() -> {
            return 9001;
        });
    }
}