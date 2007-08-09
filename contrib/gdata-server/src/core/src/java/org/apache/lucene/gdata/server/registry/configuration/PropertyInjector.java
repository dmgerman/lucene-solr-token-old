begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.gdata.server.registry.configuration
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|server
operator|.
name|registry
operator|.
name|configuration
package|;
end_package
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|utils
operator|.
name|ReflectionUtils
import|;
end_import
begin_comment
comment|/**  * PropertyInjector is used to set member variables / properties of classes via  *<i>setter</i> methods using the  * {@link org.apache.lucene.gdata.server.registry.configuration.ComponentConfiguration}  * class.  *<p>  * To populate a object with properties from a ComponentConfiguration instance  * the class or a superclass of the object to populate has to provide at least  * one setter method with a single parameter. The object to populate is set via  * the {@link PropertyInjector#setTargetObject} method. The class of the object  * will be analyzed for setter methods having a "set" prefix in their method  * name. If one of the found setter methods is annotated with  * {@link org.apache.lucene.gdata.server.registry.configuration.Requiered} this  * property is interpreted as a mandatory property. Mandatory properties must be  * available in the provided ComponentConfiguration, if not the injection will  * fail.<br>  * The  * {@link org.apache.lucene.gdata.server.registry.configuration.ComponentConfiguration}  * contains key / value pairs where the key must match the signature of the  * setter method without the 'set' prefix and must begin with a lower case  * character.<span>Key<code>bufferSize</code> does match a method signature  * of<code>setBufferSize</code></span> The type of the parameter will be  * reflected via the Reflection API and instantiated with the given value if  * possible.  *</p>  *<p>  * Setter methods without a<code>Required</code> annotation will be set if  * the property is present in the ComponentConfiguration  *</p>  *<p>This class does not support overloaded setter methods.</p>  *  * @see org.apache.lucene.gdata.server.registry.configuration.Requiered  * @see org.apache.lucene.gdata.server.registry.configuration.ComponentConfiguration  */
end_comment
begin_class
DECL|class|PropertyInjector
specifier|public
class|class
name|PropertyInjector
block|{
DECL|field|SETTER_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|SETTER_PREFIX
init|=
literal|"set"
decl_stmt|;
DECL|field|targetClass
specifier|private
name|Class
name|targetClass
decl_stmt|;
DECL|field|target
specifier|private
name|Object
name|target
decl_stmt|;
DECL|field|requieredProperties
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
name|requieredProperties
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|optionalProperties
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
name|optionalProperties
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * Sets the object to be populated with the properties provided in the ComponentConfiguration.      * @param o - the object to populate      */
DECL|method|setTargetObject
specifier|public
name|void
name|setTargetObject
parameter_list|(
specifier|final
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"TargetObject must not be null"
argument_list|)
throw|;
name|this
operator|.
name|target
operator|=
name|o
expr_stmt|;
name|this
operator|.
name|targetClass
operator|=
name|o
operator|.
name|getClass
argument_list|()
expr_stmt|;
try|try
block|{
name|registerProperties
argument_list|(
name|this
operator|.
name|targetClass
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|InjectionException
argument_list|(
literal|"can access field -- "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|this
operator|.
name|requieredProperties
operator|.
name|isEmpty
argument_list|()
operator|&&
name|this
operator|.
name|optionalProperties
operator|.
name|isEmpty
argument_list|()
condition|)
throw|throw
operator|new
name|InjectionException
argument_list|(
literal|"Given type has no public setter methods -- "
operator|+
name|o
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
DECL|method|getRequiredSize
specifier|protected
name|int
name|getRequiredSize
parameter_list|()
block|{
return|return
name|this
operator|.
name|requieredProperties
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|getOptionalSize
specifier|protected
name|int
name|getOptionalSize
parameter_list|()
block|{
return|return
name|this
operator|.
name|optionalProperties
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|registerProperties
specifier|private
name|void
name|registerProperties
parameter_list|(
specifier|final
name|Class
name|clazz
parameter_list|)
throws|throws
name|SecurityException
throws|,
name|NoSuchFieldException
block|{
if|if
condition|(
name|clazz
operator|==
literal|null
condition|)
return|return;
name|Method
index|[]
name|methodes
init|=
name|clazz
operator|.
name|getMethods
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|methodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|methodes
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|PropertyInjector
operator|.
name|SETTER_PREFIX
argument_list|)
condition|)
block|{
name|String
name|methodName
init|=
name|methodes
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|fieldName
init|=
name|getFieldName
argument_list|(
name|methodName
argument_list|)
decl_stmt|;
if|if
condition|(
name|methodes
index|[
name|i
index|]
operator|.
name|getAnnotation
argument_list|(
name|Requiered
operator|.
name|class
argument_list|)
operator|!=
literal|null
condition|)
name|this
operator|.
name|requieredProperties
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|methodes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
else|else
name|this
operator|.
name|optionalProperties
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|methodes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|registerProperties
argument_list|(
name|clazz
operator|.
name|getSuperclass
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getFieldName
specifier|private
name|String
name|getFieldName
parameter_list|(
specifier|final
name|String
name|setterMethodName
parameter_list|)
block|{
comment|// remove 'set' prefix --> first char as lowerCase
name|String
name|retVal
init|=
name|setterMethodName
operator|.
name|substring
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|String
name|firstLetter
init|=
name|retVal
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|retVal
operator|=
name|retVal
operator|.
name|replaceFirst
argument_list|(
name|firstLetter
argument_list|,
name|firstLetter
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|retVal
return|;
block|}
comment|/**      * Injects the properties stored in the<code>ComponentConfiguration</code>      * to the corresponding methods of the target object      * @param bean - configuration bean containing all properties to set.      *       */
DECL|method|injectProperties
specifier|public
name|void
name|injectProperties
parameter_list|(
specifier|final
name|ComponentConfiguration
name|bean
parameter_list|)
block|{
if|if
condition|(
name|bean
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"bean must not be null"
argument_list|)
throw|;
if|if
condition|(
name|this
operator|.
name|target
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"target is not set -- null"
argument_list|)
throw|;
name|Set
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
argument_list|>
name|requiered
init|=
name|this
operator|.
name|requieredProperties
operator|.
name|entrySet
argument_list|()
decl_stmt|;
comment|// set required properties
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
name|entry
range|:
name|requiered
control|)
block|{
if|if
condition|(
operator|!
name|bean
operator|.
name|contains
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
throw|throw
operator|new
name|InjectionException
argument_list|(
literal|"Required property can not be set -- value not in configuration bean; Property: "
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|"for class "
operator|+
name|this
operator|.
name|targetClass
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
name|populate
argument_list|(
name|bean
argument_list|,
name|entry
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
argument_list|>
name|optinal
init|=
name|this
operator|.
name|optionalProperties
operator|.
name|entrySet
argument_list|()
decl_stmt|;
comment|// set optional properties
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
name|entry
range|:
name|optinal
control|)
block|{
if|if
condition|(
name|bean
operator|.
name|contains
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
name|populate
argument_list|(
name|bean
argument_list|,
name|entry
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|populate
specifier|private
name|void
name|populate
parameter_list|(
name|ComponentConfiguration
name|bean
parameter_list|,
name|Entry
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
name|entry
parameter_list|)
block|{
name|String
name|value
init|=
name|bean
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|Method
name|m
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|parameterTypes
init|=
name|m
operator|.
name|getParameterTypes
argument_list|()
decl_stmt|;
if|if
condition|(
name|parameterTypes
operator|.
name|length
operator|>
literal|1
condition|)
throw|throw
operator|new
name|InjectionException
argument_list|(
literal|"Setter has more than one parameter "
operator|+
name|m
operator|.
name|getName
argument_list|()
operator|+
literal|" -- can not invoke method -- "
argument_list|)
throw|;
name|Object
name|parameter
init|=
literal|null
decl_stmt|;
try|try
block|{
name|parameter
operator|=
name|createObject
argument_list|(
name|value
argument_list|,
name|parameterTypes
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InjectionException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|InjectionException
argument_list|(
literal|"parameter object creation failed for method "
operator|+
name|m
operator|.
name|getName
argument_list|()
operator|+
literal|" in class: "
operator|+
name|this
operator|.
name|targetClass
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
comment|// only setters with one parameter are supported
name|Object
index|[]
name|parameters
init|=
block|{
name|parameter
block|}
decl_stmt|;
try|try
block|{
name|m
operator|.
name|invoke
argument_list|(
name|this
operator|.
name|target
argument_list|,
name|parameters
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|InjectionException
argument_list|(
literal|"Can not set value of type "
operator|+
name|value
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" -- can not invoke method -- "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|createObject
specifier|private
name|Object
name|createObject
parameter_list|(
name|String
name|s
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
block|{
try|try
block|{
comment|// if class is requested use s as fully qualified class name
if|if
condition|(
name|clazz
operator|==
name|Class
operator|.
name|class
condition|)
return|return
name|Class
operator|.
name|forName
argument_list|(
name|s
argument_list|)
return|;
comment|// check for primitive type
if|if
condition|(
name|clazz
operator|.
name|isPrimitive
argument_list|()
condition|)
name|clazz
operator|=
name|ReflectionUtils
operator|.
name|getPrimitiveWrapper
argument_list|(
name|clazz
argument_list|)
expr_stmt|;
name|boolean
name|defaultConst
init|=
literal|false
decl_stmt|;
name|boolean
name|stringConst
init|=
literal|false
decl_stmt|;
name|Constructor
index|[]
name|constructors
init|=
name|clazz
operator|.
name|getConstructors
argument_list|()
decl_stmt|;
if|if
condition|(
name|constructors
operator|.
name|length
operator|==
literal|0
condition|)
name|defaultConst
operator|=
literal|true
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|constructors
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|constructors
index|[
name|i
index|]
operator|.
name|getParameterTypes
argument_list|()
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|defaultConst
operator|=
literal|true
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|constructors
index|[
name|i
index|]
operator|.
name|getParameterTypes
argument_list|()
operator|.
name|length
operator|==
literal|1
operator|&&
name|constructors
index|[
name|i
index|]
operator|.
name|getParameterTypes
argument_list|()
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
name|String
operator|.
name|class
argument_list|)
condition|)
name|stringConst
operator|=
literal|true
expr_stmt|;
block|}
comment|/*              * if there is a string constructor use the string as a parameter              */
if|if
condition|(
name|stringConst
condition|)
block|{
name|Constructor
name|constructor
init|=
name|clazz
operator|.
name|getConstructor
argument_list|(
operator|new
name|Class
index|[]
block|{
name|String
operator|.
name|class
block|}
argument_list|)
decl_stmt|;
return|return
name|constructor
operator|.
name|newInstance
argument_list|(
operator|new
name|Object
index|[]
block|{
name|s
block|}
argument_list|)
return|;
block|}
comment|/*              * if no string const. but a default const. -- use the string as a              * class name              */
if|if
condition|(
name|defaultConst
condition|)
return|return
name|Class
operator|.
name|forName
argument_list|(
name|s
argument_list|)
operator|.
name|newInstance
argument_list|()
return|;
throw|throw
operator|new
name|InjectionException
argument_list|(
literal|"Parameter can not be created -- no default or String constructor found for class "
operator|+
name|clazz
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|InjectionException
argument_list|(
literal|"can not create object for setter"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Sets all members to their default values and clears the internal used      * {@link Map} instances      *       * @see Map#clear()      */
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|this
operator|.
name|target
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|targetClass
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|optionalProperties
operator|.
name|clear
argument_list|()
expr_stmt|;
name|this
operator|.
name|requieredProperties
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
