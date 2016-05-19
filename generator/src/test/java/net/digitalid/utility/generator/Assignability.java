package net.digitalid.utility.generator;

import java.util.List;

import net.digitalid.utility.annotations.method.Pure;
import net.digitalid.utility.generator.annotations.generators.GenerateSubclass;
import net.digitalid.utility.validation.annotations.testing.AssignableTo;
import net.digitalid.utility.validation.annotations.testing.UnassignableTo;
import net.digitalid.utility.validation.annotations.type.Mutable;

@Mutable
@GenerateSubclass
@SuppressWarnings("PublicField")
public abstract class Assignability {
    
    /* -------------------------------------------------- Objects -------------------------------------------------- */
    
    @Pure
    public abstract @AssignableTo(String.class) String getStringAssignableToString();
    
    @Pure
    public abstract @AssignableTo(CharSequence.class) String getStringAssignableToCharSequence();
    
    @Pure
    public abstract @AssignableTo(Comparable.class) String getStringAssignableToComparable();
    
    @Pure
    public abstract @UnassignableTo(Integer.class) String getStringUnassignableToInteger();
    
    @Pure
    public abstract @UnassignableTo(int.class) String getStringUnassignableToInt();
    
    @Pure
    public abstract @UnassignableTo(List.class) String getStringUnassignableToList();
    
    @Pure
    public abstract @UnassignableTo(String[].class) String getStringUnassignableToStringArray();
    
    @Pure
    public abstract @UnassignableTo(String[][].class) String getStringUnassignableToArrayOfStringArrays();
    
    /* -------------------------------------------------- Primitives -------------------------------------------------- */
    
    @Pure
    public abstract @AssignableTo(int.class) int getIntAssignableToInt();
    
    @Pure
    public abstract @AssignableTo(Integer.class) int getIntAssignableToInteger();
    
    @Pure
    public abstract @AssignableTo(int.class) Integer getIntegerAssignableToInt();
    
    @Pure
    public abstract @AssignableTo(Comparable.class) int getIntAssignableToComparable();
    
    @Pure
    public abstract @AssignableTo(long.class) int getIntAssignableToLong();
    
    @Pure
    public abstract @UnassignableTo(short.class) int getIntUnassignableToShort();
    
    @Pure
    public abstract @UnassignableTo(String.class) int getIntUnassignableToString();
    
    @Pure
    public abstract @UnassignableTo(List.class) int getIntUnassignableToList();
    
    @Pure
    public abstract @UnassignableTo(int[].class) int getIntUnassignableToIntArray();
    
    @Pure
    public abstract @UnassignableTo(int[][].class) int getIntUnassignableToArrayOfIntArrays();
    
    /* -------------------------------------------------- Arrays -------------------------------------------------- */
    
    @Pure
    public abstract @AssignableTo(int[].class) int[] getIntArrayAssignableToIntArray();
    
    @Pure
    public abstract @AssignableTo(String[].class) String[] getStringArrayAssignableToStringArray();
    
    @Pure
    public abstract @AssignableTo(Object[].class) String[] getStringArrayAssignableToObjectArray();
    
    @Pure
    public abstract @UnassignableTo(int.class) int[] getIntArrayUnassignableToInt();
    
    @Pure
    public abstract @UnassignableTo(long[].class) int[] getIntArrayUnassignableToLongArray();
    
    @Pure
    public abstract @UnassignableTo(Integer[].class) int[] getIntArrayUnassignableToIntegerArray();
    
    @Pure
    public abstract @UnassignableTo(int[][].class) int[] getIntArrayUnassignableToArrayOfIntArrays();
    
    @Pure
    public abstract @UnassignableTo(String.class) String[] getStringArrayUnassignableToString();
    
    @Pure
    public abstract @UnassignableTo(String[].class) Object[] getObjectArrayUnassignableToStringArray();
    
    @Pure
    public abstract @UnassignableTo(String[][].class) String[] getStringArrayUnassignableToArrayOfStringArrays();
    
}
