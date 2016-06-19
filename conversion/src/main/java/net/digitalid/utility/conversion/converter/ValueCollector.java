package net.digitalid.utility.conversion.converter;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import net.digitalid.utility.annotations.method.Impure;
import net.digitalid.utility.functional.interfaces.Consumer;
import net.digitalid.utility.validation.annotations.size.MaxSize;
import net.digitalid.utility.validation.annotations.size.Size;

/**
 *
 */
public interface ValueCollector {
    
    /* -------------------------------------------------- Setters -------------------------------------------------- */
    
    /**
     * Sets the next parameter to empty.
     */
    @Impure
    public void setEmpty();
    
    /**
     * Sets the next parameter to the given boolean value.
     * 
     * @param value the boolean value which is to be set.
     */
    @Impure
    public void setBoolean(boolean value);
    
    /**
     * Sets the next parameter to the given byte value.
     * 
     * @param value the byte value which is to be set.
     */
    @Impure
    public void setInteger08(byte value);
    
    /**
     * Sets the next parameter to the given short value.
     * 
     * @param value the short value which is to be set.
     */
    @Impure
    public void setInteger16(short value);
    
    /**
     * Sets the next parameter to the given int value.
     * 
     * @param value the int value which is to be set.
     */
    @Impure
    public void setInteger32(int value);
    
    /**
     * Sets the next parameter to the given long value.
     * 
     * @param value the long value which is to be set.
     */
    @Impure
    public void setInteger64(long value);
    
    /**
     * Sets the next parameter to the given integer value.
     * 
     * @param value the integer value which is to be set.
     */
    @Impure
    public void setInteger(@Nonnull BigInteger value);
    
    /**
     * Sets the next parameter to the given float value.
     * 
     * @param value the float value which is to be set.
     */
    @Impure
    public void setDecimal32(float value);
    
    /**
     * Sets the next parameter to the given double value.
     * 
     * @param value the double value which is to be set.
     */
    @Impure
    public void setDecimal64(double value);
    
    /**
     * Sets the next parameter to the given char value.
     * 
     * @param value the char value which is to be set.
     */
    @Impure
    public void setString01(char value);
    
    /**
     * Sets the next parameter to the given string value.
     * 
     * @param value the string value which is to be set.
     */
    @Impure
    public void setString64(@Nonnull @MaxSize(64) String value);
    
    /**
     * Sets the next parameter to the given string value.
     * 
     * @param value the string value which is to be set.
     */
    @Impure
    public void setString(@Nonnull String value);
    
    /**
     * Sets the next parameter to the given binary value.
     * 
     * @param value the binary value which is to be set.
     */
    @Impure
    public void setBinary128(@Nonnull @Size(16) byte[] value);
    
    /**
     * Sets the next parameter to the given binary value.
     * 
     * @param value the binary value which is to be set.
     */
    @Impure
    public void setBinary256(@Nonnull @Size(32) byte[] value);
    
    /**
     * Sets the next parameter to the given binary value.
     * 
     * @param value the binary value which is to be set.
     */
    @Impure
    public void setBinary(@Nonnull byte[] value);
    
    /**
     * Sets the next parameter to the given input stream.
     * 
     * @param stream the input stream which is to be set.
     * @param length the number of bytes in the input stream.
     * 
     * @require Database.getInstance().supportsBinaryStreams() : "The database supports binary streams.";
     */
    @Impure
    public void setBinaryStream(@Nonnull InputStream stream, int length);
    
    @Impure
    public <T> void setList(@Nonnull List<T> value, Consumer<T> entityCollector);
    
    @Impure
    public <T> void setArray(@Nonnull T[] value, Consumer<T> entityCollector);
    
    @Impure
    public <T> void setSet(@Nonnull Set<T> value, Consumer<T> entityCollector);
    
    @Impure
    public <K, V> void setMap(@Nonnull Map<K, V> value, Consumer<K> genericTypeKey, Consumer<V> genericTypeValue);
    
    /* -------------------------------------------------- Null -------------------------------------------------- */
    
    /**
     * Sets the next parameter to null.
     */
    @Impure
    public void setNull();
    
}
