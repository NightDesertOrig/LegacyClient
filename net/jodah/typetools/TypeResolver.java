package net.jodah.typetools;

import java.lang.reflect.AccessibleObject;
import java.security.AccessController;
import java.lang.reflect.Field;
import java.security.PrivilegedExceptionAction;
import sun.misc.Unsafe;
import java.util.Collections;
import java.util.WeakHashMap;
import java.lang.reflect.Member;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.lang.reflect.Modifier;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.ref.Reference;
import java.util.Map;

public final class TypeResolver
{
    private static final Map<Class<?>, Reference<Map<TypeVariable<?>, Type>>> TYPE_VARIABLE_CACHE;
    private static volatile boolean CACHE_ENABLED;
    private static boolean RESOLVES_LAMBDAS;
    private static Method GET_CONSTANT_POOL;
    private static Method GET_CONSTANT_POOL_SIZE;
    private static Method GET_CONSTANT_POOL_METHOD_AT;
    private static final Map<String, Method> OBJECT_METHODS;
    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPERS;
    private static final Double JAVA_VERSION;
    
    public static void enableCache() {
        TypeResolver.CACHE_ENABLED = /*EL:125*/true;
    }
    
    public static void disableCache() {
        TypeResolver.TYPE_VARIABLE_CACHE.clear();
        TypeResolver.CACHE_ENABLED = /*EL:133*/false;
    }
    
    public static <T, S extends T> Class<?> resolveRawArgument(final Class<T> a1, final Class<S> a2) {
        /*SL:146*/return resolveRawArgument(resolveGenericType(a1, a2), a2);
    }
    
    public static Class<?> resolveRawArgument(final Type a1, final Class<?> a2) {
        final Class<?>[] v1 = resolveRawArguments(/*EL:160*/a1, a2);
        /*SL:161*/if (v1 == null) {
            /*SL:162*/return Unknown.class;
        }
        /*SL:164*/if (v1.length != 1) {
            /*SL:165*/throw new IllegalArgumentException("Expected 1 argument for generic type " + a1 + " but found " + v1.length);
        }
        /*SL:168*/return v1[0];
    }
    
    public static <T, S extends T> Class<?>[] resolveRawArguments(final Class<T> a1, final Class<S> a2) {
        /*SL:182*/return resolveRawArguments(resolveGenericType(a1, a2), a2);
    }
    
    public static Class<?>[] resolveRawArguments(final Type v-3, final Class<?> v-2) {
        Class<?>[] array = /*EL:196*/null;
        Class<?> v0 = /*EL:197*/null;
        /*SL:200*/if (TypeResolver.RESOLVES_LAMBDAS && v-2.isSynthetic()) {
            final Class<?> a1 = /*EL:201*/(Class<?>)((v-3 instanceof ParameterizedType && /*EL:202*/((ParameterizedType)v-3).getRawType() instanceof Class) ? ((ParameterizedType)v-3).getRawType() : /*EL:203*/((v-3 instanceof Class) ? ((Class)v-3) : null));
            /*SL:205*/if (a1 != null && a1.isInterface()) {
                /*SL:206*/v0 = a1;
            }
        }
        /*SL:209*/if (v-3 instanceof ParameterizedType) {
            final ParameterizedType v = /*EL:210*/(ParameterizedType)v-3;
            final Type[] v2 = /*EL:211*/v.getActualTypeArguments();
            /*SL:212*/array = (Class<?>[])new Class[v2.length];
            /*SL:213*/for (int a2 = 0; a2 < v2.length; ++a2) {
                /*SL:214*/array[a2] = resolveRawClass(v2[a2], v-2, v0);
            }
        }
        else/*SL:215*/ if (v-3 instanceof TypeVariable) {
            /*SL:216*/array = (Class<?>[])new Class[] { resolveRawClass(/*EL:217*/v-3, v-2, v0) };
        }
        else/*SL:218*/ if (v-3 instanceof Class) {
            final TypeVariable<?>[] a3 = /*EL:219*/(TypeVariable<?>[])((Class)v-3).getTypeParameters();
            /*SL:220*/array = (Class<?>[])new Class[a3.length];
            /*SL:221*/for (int v3 = 0; v3 < a3.length; ++v3) {
                /*SL:222*/array[v3] = resolveRawClass(a3[v3], v-2, v0);
            }
        }
        /*SL:225*/return array;
    }
    
    public static Type resolveGenericType(final Class<?> v-6, final Type v-5) {
        Class<?> a1;
        /*SL:238*/if (v-5 instanceof ParameterizedType) {
            /*SL:239*/a1 = (Class<?>)((ParameterizedType)v-5).getRawType();
        }
        else {
            /*SL:241*/a1 = (Class<?>)v-5;
        }
        /*SL:243*/if (v-6.equals(a1)) {
            /*SL:244*/return v-5;
        }
        /*SL:247*/if (v-6.isInterface()) {
            /*SL:248*/for (final Type v1 : a1.getGenericInterfaces()) {
                final Type a2;
                /*SL:249*/if (v1 != null && !v1.equals(Object.class) && /*EL:250*/(a2 = resolveGenericType(v-6, v1)) != null) {
                    /*SL:251*/return a2;
                }
            }
        }
        final Type genericSuperclass = /*EL:254*/a1.getGenericSuperclass();
        final Type resolveGenericType;
        /*SL:255*/if (genericSuperclass != null && !genericSuperclass.equals(Object.class) && /*EL:256*/(resolveGenericType = resolveGenericType(v-6, genericSuperclass)) != null) {
            /*SL:257*/return resolveGenericType;
        }
        /*SL:259*/return null;
    }
    
    public static Class<?> resolveRawClass(final Type a1, final Class<?> a2) {
        /*SL:271*/return resolveRawClass(a1, a2, null);
    }
    
    private static Class<?> resolveRawClass(Type v1, final Class<?> v2, final Class<?> v3) {
        /*SL:275*/if (v1 instanceof Class) {
            /*SL:276*/return (Class<?>)v1;
        }
        /*SL:277*/if (v1 instanceof ParameterizedType) {
            /*SL:278*/return resolveRawClass(((ParameterizedType)v1).getRawType(), v2, v3);
        }
        /*SL:279*/if (v1 instanceof GenericArrayType) {
            final GenericArrayType a1 = /*EL:280*/(GenericArrayType)v1;
            final Class<?> a2 = resolveRawClass(/*EL:281*/a1.getGenericComponentType(), v2, v3);
            /*SL:282*/return Array.newInstance(a2, 0).getClass();
        }
        /*SL:283*/if (v1 instanceof TypeVariable) {
            final TypeVariable<?> a3 = /*EL:284*/(TypeVariable<?>)v1;
            /*SL:285*/v1 = getTypeVariableMap(v2, v3).get(a3);
            /*SL:287*/v1 = ((v1 == null) ? resolveBound(a3) : resolveRawClass(v1, v2, v3));
        }
        /*SL:290*/return (Class<?>)((v1 instanceof Class) ? ((Class)v1) : Unknown.class);
    }
    
    private static Map<TypeVariable<?>, Type> getTypeVariableMap(final Class<?> v1, final Class<?> v2) {
        final Reference<Map<TypeVariable<?>, Type>> v3 = TypeResolver.TYPE_VARIABLE_CACHE.get(/*EL:295*/v1);
        Map<TypeVariable<?>, Type> v4 = /*EL:296*/(v3 != null) ? v3.get() : null;
        /*SL:298*/if (v4 == null) {
            /*SL:299*/v4 = new HashMap<TypeVariable<?>, Type>();
            /*SL:302*/if (v2 != null) {
                populateLambdaArgs(/*EL:303*/v2, v1, v4);
            }
            populateSuperTypeArgs(/*EL:306*/v1.getGenericInterfaces(), v4, v2 != null);
            Type a1 = /*EL:309*/v1.getGenericSuperclass();
            /*SL:311*/for (Class<?> a2 = v1.getSuperclass(); a2 != null && !Object.class.equals(a2); /*SL:317*/a2 = a2.getSuperclass()) {
                if (a1 instanceof ParameterizedType) {
                    populateTypeArgs((ParameterizedType)a1, v4, false);
                }
                populateSuperTypeArgs(a2.getGenericInterfaces(), v4, false);
                a1 = a2.getGenericSuperclass();
            }
            /*SL:322*/for (Class<?> a2 = v1; a2.isMemberClass(); /*SL:327*/a2 = a2.getEnclosingClass()) {
                a1 = a2.getGenericSuperclass();
                if (a1 instanceof ParameterizedType) {
                    populateTypeArgs((ParameterizedType)a1, v4, v2 != null);
                }
            }
            /*SL:330*/if (TypeResolver.CACHE_ENABLED) {
                TypeResolver.TYPE_VARIABLE_CACHE.put(/*EL:331*/v1, new WeakReference<Map<TypeVariable<?>, Type>>(v4));
            }
        }
        /*SL:334*/return v4;
    }
    
    private static void populateSuperTypeArgs(final Type[] v1, final Map<TypeVariable<?>, Type> v2, final boolean v3) {
        /*SL:342*/for (Type a3 : v1) {
            /*SL:343*/if (a3 instanceof ParameterizedType) {
                final ParameterizedType a2 = /*EL:344*/(ParameterizedType)a3;
                /*SL:345*/if (!v3) {
                    populateTypeArgs(/*EL:346*/a2, v2, v3);
                }
                /*SL:347*/a3 = a2.getRawType();
                /*SL:348*/if (a3 instanceof Class) {
                    populateSuperTypeArgs(/*EL:349*/((Class)a3).getGenericInterfaces(), v2, v3);
                }
                /*SL:350*/if (v3) {
                    populateTypeArgs(/*EL:351*/a2, v2, v3);
                }
            }
            else/*SL:352*/ if (a3 instanceof Class) {
                populateSuperTypeArgs(/*EL:353*/((Class)a3).getGenericInterfaces(), v2, v3);
            }
        }
    }
    
    private static void populateTypeArgs(final ParameterizedType v-8, final Map<TypeVariable<?>, Type> v-7, final boolean v-6) {
        /*SL:362*/if (v-8.getRawType() instanceof Class) {
            final TypeVariable<?>[] typeParameters = /*EL:363*/(TypeVariable<?>[])((Class)v-8.getRawType()).getTypeParameters();
            final Type[] actualTypeArguments = /*EL:364*/v-8.getActualTypeArguments();
            /*SL:366*/if (v-8.getOwnerType() != null) {
                final Type a1 = /*EL:367*/v-8.getOwnerType();
                /*SL:368*/if (a1 instanceof ParameterizedType) {
                    populateTypeArgs(/*EL:369*/(ParameterizedType)a1, v-7, v-6);
                }
            }
            /*SL:372*/for (int i = 0; i < actualTypeArguments.length; ++i) {
                final TypeVariable<?> typeVariable = /*EL:373*/typeParameters[i];
                final Type type = /*EL:374*/actualTypeArguments[i];
                /*SL:376*/if (type instanceof Class) {
                    /*SL:377*/v-7.put(typeVariable, type);
                }
                else/*SL:378*/ if (type instanceof GenericArrayType) {
                    /*SL:379*/v-7.put(typeVariable, type);
                }
                else/*SL:380*/ if (type instanceof ParameterizedType) {
                    /*SL:381*/v-7.put(typeVariable, type);
                }
                else/*SL:382*/ if (type instanceof TypeVariable) {
                    final TypeVariable<?> a2 = /*EL:383*/(TypeVariable<?>)type;
                    /*SL:384*/if (v-6) {
                        final Type a3 = /*EL:385*/v-7.get(typeVariable);
                        /*SL:386*/if (a3 != null) {
                            /*SL:387*/v-7.put(a2, a3);
                            /*SL:388*/continue;
                        }
                    }
                    Type v1 = /*EL:392*/v-7.get(a2);
                    /*SL:393*/if (v1 == null) {
                        /*SL:394*/v1 = resolveBound(a2);
                    }
                    /*SL:395*/v-7.put(typeVariable, v1);
                }
            }
        }
    }
    
    public static Type resolveBound(final TypeVariable<?> a1) {
        final Type[] v1 = /*EL:405*/a1.getBounds();
        /*SL:406*/if (v1.length == 0) {
            /*SL:407*/return Unknown.class;
        }
        Type v2 = /*EL:409*/v1[0];
        /*SL:410*/if (v2 instanceof TypeVariable) {
            /*SL:411*/v2 = resolveBound((TypeVariable<?>)v2);
        }
        /*SL:413*/return (v2 == Object.class) ? Unknown.class : v2;
    }
    
    private static void populateLambdaArgs(final Class<?> v-6, final Class<?> v-5, final Map<TypeVariable<?>, Type> v-4) {
        /*SL:421*/if (TypeResolver.RESOLVES_LAMBDAS) {
            /*SL:423*/for (final Method v0 : v-6.getMethods()) {
                /*SL:424*/if (!isDefaultMethod(v0) && !Modifier.isStatic(v0.getModifiers()) && !v0.isBridge()) {
                    final Method v = TypeResolver.OBJECT_METHODS.get(/*EL:426*/v0.getName());
                    /*SL:427*/if (v == null || !Arrays.equals(v0.getTypeParameters(), v.getTypeParameters())) {
                        final Type v2 = /*EL:431*/v0.getGenericReturnType();
                        final Type[] v3 = /*EL:432*/v0.getGenericParameterTypes();
                        final Member v4 = getMemberRef(/*EL:434*/v-5);
                        /*SL:435*/if (v4 == null) {
                            /*SL:436*/return;
                        }
                        /*SL:439*/if (v2 instanceof TypeVariable) {
                            Class<?> a1 = /*EL:440*/(v4 instanceof Method) ? ((Method)v4).getReturnType() : ((Constructor)v4).getDeclaringClass();
                            /*SL:442*/a1 = wrapPrimitives(a1);
                            /*SL:443*/if (!a1.equals(Void.class)) {
                                /*SL:444*/v-4.put((TypeVariable<?>)v2, a1);
                            }
                        }
                        final Class<?>[] a2 = /*EL:447*/(v4 instanceof Method) ? ((Method)v4).getParameterTypes() : ((Constructor)v4).getParameterTypes();
                        int v5 = /*EL:451*/0;
                        /*SL:452*/if (v3.length > 0 && v3[0] instanceof TypeVariable && v3.length == a2.length + 1) {
                            final Class<?> a3 = /*EL:454*/v4.getDeclaringClass();
                            /*SL:455*/v-4.put((TypeVariable<?>)v3[0], a3);
                            /*SL:456*/v5 = 1;
                        }
                        int v6 = /*EL:460*/0;
                        /*SL:461*/if (v3.length < a2.length) {
                            /*SL:462*/v6 = a2.length - v3.length;
                        }
                        /*SL:466*/for (int a4 = 0; a4 + v6 < a2.length; ++a4) {
                            /*SL:467*/if (v3[a4] instanceof TypeVariable) {
                                /*SL:468*/v-4.put((TypeVariable<?>)v3[a4 + v5], wrapPrimitives(a2[a4 + v6]));
                            }
                        }
                        /*SL:471*/return;
                    }
                }
            }
        }
    }
    
    private static boolean isDefaultMethod(final Method a1) {
        /*SL:478*/return TypeResolver.JAVA_VERSION >= 1.8 && a1.isDefault();
    }
    
    private static Member getMemberRef(final Class<?> v-1) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: getstatic       net/jodah/typetools/TypeResolver.GET_CONSTANT_POOL:Ljava/lang/reflect/Method;
        //     3: aload_0         /* v-1 */
        //     4: iconst_0       
        //     5: anewarray       Ljava/lang/Object;
        //     8: invokevirtual   java/lang/reflect/Method.invoke:(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
        //    11: astore_1        /* a1 */
        //    12: goto            18
        //    15: astore_2        /* v1 */
        //    16: aconst_null    
        //    17: areturn        
        //    18: aconst_null    
        //    19: astore_2        /* v1 */
        //    20: aload_1         /* v0 */
        //    21: invokestatic    net/jodah/typetools/TypeResolver.getConstantPoolSize:(Ljava/lang/Object;)I
        //    24: iconst_1       
        //    25: isub           
        //    26: istore_3        /* v2 */
        //    27: iload_3         /* v2 */
        //    28: iflt            118
        //    31: aload_1         /* v0 */
        //    32: iload_3         /* v2 */
        //    33: invokestatic    net/jodah/typetools/TypeResolver.getConstantPoolMethodAt:(Ljava/lang/Object;I)Ljava/lang/reflect/Member;
        //    36: astore          v3
        //    38: aload           v3
        //    40: ifnull          112
        //    43: aload           v3
        //    45: instanceof      Ljava/lang/reflect/Constructor;
        //    48: ifeq            70
        //    51: aload           v3
        //    53: invokeinterface java/lang/reflect/Member.getDeclaringClass:()Ljava/lang/Class;
        //    58: invokevirtual   java/lang/Class.getName:()Ljava/lang/String;
        //    61: ldc_w           "java.lang.invoke.SerializedLambda"
        //    64: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //    67: ifne            112
        //    70: aload           v3
        //    72: invokeinterface java/lang/reflect/Member.getDeclaringClass:()Ljava/lang/Class;
        //    77: aload_0         /* v-1 */
        //    78: invokevirtual   java/lang/Class.isAssignableFrom:(Ljava/lang/Class;)Z
        //    81: ifeq            87
        //    84: goto            112
        //    87: aload           v3
        //    89: astore_2        /* v1 */
        //    90: aload           v3
        //    92: instanceof      Ljava/lang/reflect/Method;
        //    95: ifeq            118
        //    98: aload           v3
        //   100: checkcast       Ljava/lang/reflect/Method;
        //   103: invokestatic    net/jodah/typetools/TypeResolver.isAutoBoxingMethod:(Ljava/lang/reflect/Method;)Z
        //   106: ifne            112
        //   109: goto            118
        //   112: iinc            v2, -1
        //   115: goto            27
        //   118: aload_2         /* v1 */
        //   119: areturn        
        //    Signature:
        //  (Ljava/lang/Class<*>;)Ljava/lang/reflect/Member;
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------
        //  12     3       1     a1    Ljava/lang/Object;
        //  16     2       2     v1    Ljava/lang/Exception;
        //  38     74      4     v3    Ljava/lang/reflect/Member;
        //  27     91      3     v2    I
        //  0      120     0     v-1   Ljava/lang/Class;
        //  18     102     1     v0    Ljava/lang/Object;
        //  20     100     2     v1    Ljava/lang/reflect/Member;
        //    LocalVariableTypeTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------
        //  0      120     0     v-1   Ljava/lang/Class<*>;
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  0      12     15     18     Ljava/lang/Exception;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        //     at com.strobel.decompiler.ast.AstBuilder.convertLocalVariables(AstBuilder.java:2987)
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2446)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:109)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:210)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:757)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:655)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:532)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:499)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:141)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:130)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:105)
        //     at cuchaz.enigma.Deobfuscator.getSourceTree(Deobfuscator.java:224)
        //     at cuchaz.enigma.Deobfuscator.writeSources(Deobfuscator.java:306)
        //     at cuchaz.enigma.gui.GuiController$1.run(GuiController.java:110)
        //     at cuchaz.enigma.gui.ProgressDialog$1.run(ProgressDialog.java:98)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private static boolean isAutoBoxingMethod(final Method a1) {
        final Class<?>[] v1 = /*EL:510*/a1.getParameterTypes();
        /*SL:511*/return a1.getName().equals("valueOf") && v1.length == 1 && v1[0].isPrimitive() && wrapPrimitives(v1[0]).equals(/*EL:512*/a1.getDeclaringClass());
    }
    
    private static Class<?> wrapPrimitives(final Class<?> a1) {
        /*SL:516*/return a1.isPrimitive() ? TypeResolver.PRIMITIVE_WRAPPERS.get(a1) : a1;
    }
    
    private static int getConstantPoolSize(final Object v1) {
        try {
            /*SL:521*/return (int)TypeResolver.GET_CONSTANT_POOL_SIZE.invoke(v1, new Object[0]);
        }
        catch (Exception a1) {
            /*SL:523*/return 0;
        }
    }
    
    private static Member getConstantPoolMethodAt(final Object a2, final int v1) {
        try {
            /*SL:529*/return (Member)TypeResolver.GET_CONSTANT_POOL_METHOD_AT.invoke(a2, v1);
        }
        catch (Exception a3) {
            /*SL:531*/return null;
        }
    }
    
    static {
        TYPE_VARIABLE_CACHE = Collections.<Class<?>, Reference<Map<TypeVariable<?>, Type>>>synchronizedMap(new WeakHashMap<Class<?>, Reference<Map<TypeVariable<?>, Type>>>());
        TypeResolver.CACHE_ENABLED = true;
        OBJECT_METHODS = new HashMap<String, Method>();
        JAVA_VERSION = Double.parseDouble(System.getProperty("java.specification.version", "0"));
        try {
            final Unsafe unsafe = AccessController.<Unsafe>doPrivileged((PrivilegedExceptionAction<Unsafe>)new PrivilegedExceptionAction<Unsafe>() {
                @Override
                public Unsafe run() throws Exception {
                    final Field v1 = /*EL:67*/Unsafe.class.getDeclaredField("theUnsafe");
                    /*SL:68*/v1.setAccessible(true);
                    /*SL:70*/return (Unsafe)v1.get(null);
                }
            });
            TypeResolver.GET_CONSTANT_POOL = Class.class.getDeclaredMethod("getConstantPool", (Class<?>[])new Class[0]);
            final String s = (TypeResolver.JAVA_VERSION < 9.0) ? "sun.reflect.ConstantPool" : "jdk.internal.reflect.ConstantPool";
            final Class<?> forName = Class.forName(s);
            TypeResolver.GET_CONSTANT_POOL_SIZE = forName.getDeclaredMethod("getSize", (Class<?>[])new Class[0]);
            TypeResolver.GET_CONSTANT_POOL_METHOD_AT = forName.getDeclaredMethod("getMethodAt", Integer.TYPE);
            final Field declaredField = AccessibleObject.class.getDeclaredField("override");
            final long objectFieldOffset = unsafe.objectFieldOffset(declaredField);
            unsafe.putBoolean(TypeResolver.GET_CONSTANT_POOL, objectFieldOffset, true);
            unsafe.putBoolean(TypeResolver.GET_CONSTANT_POOL_SIZE, objectFieldOffset, true);
            unsafe.putBoolean(TypeResolver.GET_CONSTANT_POOL_METHOD_AT, objectFieldOffset, true);
            final Object invoke = TypeResolver.GET_CONSTANT_POOL.invoke(Object.class, new Object[0]);
            TypeResolver.GET_CONSTANT_POOL_SIZE.invoke(invoke, new Object[0]);
            for (final Method v1 : Object.class.getDeclaredMethods()) {
                TypeResolver.OBJECT_METHODS.put(v1.getName(), v1);
            }
            TypeResolver.RESOLVES_LAMBDAS = true;
        }
        catch (Exception ex) {}
        final Map<Class<?>, Class<?>> map = new HashMap<Class<?>, Class<?>>();
        map.put(Boolean.TYPE, Boolean.class);
        map.put(Byte.TYPE, Byte.class);
        map.put(Character.TYPE, Character.class);
        map.put(Double.TYPE, Double.class);
        map.put(Float.TYPE, Float.class);
        map.put(Integer.TYPE, Integer.class);
        map.put(Long.TYPE, Long.class);
        map.put(Short.TYPE, Short.class);
        map.put(Void.TYPE, Void.class);
        PRIMITIVE_WRAPPERS = Collections.<Class<?>, Class<?>>unmodifiableMap((Map<? extends Class<?>, ? extends Class<?>>)map);
    }
    
    public static final class Unknown
    {
    }
}
