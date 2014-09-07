package org.ObjectLayout;

import org.ObjectLayout.intrinsifiable.PrimitiveArray;

import java.lang.reflect.Constructor;

/**
 * A builder used for instantiating a {@link PrimitiveArray}&ltT&gt
 * <p>
 * {@link org.ObjectLayout.PrimitiveArrayBuilder} follows the commonly used builder pattern, and is useful for
 * capturing the instantiation parameters of {@link org.ObjectLayout.StructuredArray}s.
 * </p>
 * <p>
 * {@link org.ObjectLayout.PrimitiveArrayBuilder}s can be created for "flat" and "nested" {@link org.ObjectLayout.StructuredArray}
 * constructs, and can range from the simplest forms used when default constructors are employed, to forms that
 * supply customized per-element construction arguments that can take construction context (such as index,
 * containing array, and arbitrary data passed in a contextCookie) into account.
 * </p>
 * A simple example of using a {@link org.ObjectLayout.PrimitiveArrayBuilder} to instantiate a PrimitiveLongArray is:
 * <p><blockquote><pre>
 * StructuredArray&ltMyElementClass&gt array =
 *      new PrimitiveArrayBuilder(PrimitiveLongArray.class, length).
 *          build();
 * </pre></blockquote></p>
 * <p>
 * An example of passing specific construction arguments to the array constructor is:
 * <p><blockquote><pre>
 * Constructor&ltMyArrayClass&gt constructor = MyArrayClass.class.getConstructor(int.Class, int.Class);
 *
 * StructuredArray&ltMyElementClass&gt array =
 *      new PrimitiveArrayBuilder(
 *          MyArrayClass.class,
 *          length).
 *          arrayCtorAndArgs(constructor, initArg1, initArg2).
 *          build();
 * </pre></blockquote></p>
 *
 * @param <S> The class of the PrimitiveArray that is to be instantiated by the builder
 */
public class PrimitiveArrayBuilder<S extends PrimitiveArray> {
    private static final Class[] EMPTY_ARG_TYPES = new Class[0];

    private final PrimitiveArrayModel<S> arrayModel;
    private CtorAndArgs<S> arrayCtorAndArgs;

    /**
     * Constructs a new {@link org.ObjectLayout.PrimitiveArrayBuilder} object for creating arrays of
     * type S with the given length.
     *
     * @param arrayClass The class of the array to be built by this builder
     * @param length The length of the array to be build by this builder
     */
    public PrimitiveArrayBuilder(final Class<S> arrayClass,
                                 final long length) {
        this.arrayModel = new PrimitiveArrayModel<S>(arrayClass, length);
        if ((length < 0) || (length > Integer.MAX_VALUE)) {
            throw new IllegalArgumentException("Cannot model PrimitiveArrays with length > Integer.MAX_VALUE");
        }
    }

    /**
     * Set the {@link org.ObjectLayout.CtorAndArgs} to be used in constructing arrays.
     * Setting the means for array construction is Required if the array class (S) does not support a
     * default constructor, or if a non-default construction of the array instance is needed.
     *
     * @param arrayCtorAndArgs The constructor and arguments used for constructing arrays
     * @return The builder
     */
    public PrimitiveArrayBuilder<S> arrayCtorAndArgs(final CtorAndArgs<S> arrayCtorAndArgs) {
        this.arrayCtorAndArgs = arrayCtorAndArgs;
        return this;
    }

    /**
     * Set the {@link java.lang.reflect.Constructor} and construction arguments to be used in constructing arrays.
     * Setting the means for array construction is Required if the array class (S) does not support a
     * default constructor, or if a non-default construction of the array instance is needed.
     *
     * @param constructor The constructor used for constructing arrays
     * @param args The construction arguments supplied for the constructor
     * @return The builder
     */
    public PrimitiveArrayBuilder<S> arrayCtorAndArgs(final Constructor<S> constructor, final Object... args) {
        this.arrayCtorAndArgs = new CtorAndArgs<S>(constructor, args);
        return this;
    }

    /**
     * Resolve any not-yet-resolved constructor information needed by this builder. Calling resolve() is not
     * necessary ahead of building, but it is useful for ensuring resolution works ahead of actual building
     * attempts.
     *
     * @return This builder
     * @throws NoSuchMethodException if the array constructor or element constructor fail to resolve given
     * the current information in the builder
     */
    public PrimitiveArrayBuilder<S> resolve() throws NoSuchMethodException {
        if (arrayCtorAndArgs == null) {
            arrayCtorAndArgs = new CtorAndArgs<S>(arrayModel.getArrayClass(), EMPTY_ARG_TYPES, (Object[]) null);
        }

        return this;
    }

    /**
     * Build a {@link PrimitiveArray} according to the information captured in this builder
     * @return A newly instantiated {@link PrimitiveArray}
     *
     * @throws NoSuchMethodException if the array constructor or element constructor fail to resolve given
     * the current information in the builder
     */
    public S build() throws NoSuchMethodException {
        resolve();
        int length = (int) arrayModel.getLength(); // Already verified range at instantiation
        return PrimitiveArray.newInstance(length, arrayCtorAndArgs.getConstructor(), arrayCtorAndArgs.getArgs());
    }

    /**
     * Get the {@link PrimitiveArrayModel} that describes the arrays built by this builder
     *
     * @return The {@link PrimitiveArrayModel} that describes the arrays built by this builder
     */
    public PrimitiveArrayModel<S> getArrayModel() {
        return arrayModel;
    }

    /**
     * Get the {@link CtorAndArgs} describing the constructor and arguments used to instantiate arrays with
     * this builder. May be null if non of {@link PrimitiveArrayBuilder#arrayCtorAndArgs},
     * {@link PrimitiveArrayBuilder#resolve()} or
     * {@link PrimitiveArrayBuilder#build()} have been called yet.
     * @return The {@link CtorAndArgs} describing the constructor and arguments used to instantiate arrays with
     * this builder.
     */
    public CtorAndArgs<S> getArrayCtorAndArgs() {
        return arrayCtorAndArgs;
    }
}
