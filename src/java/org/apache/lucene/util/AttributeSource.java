begin_unit
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|IdentityHashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|analysis
operator|.
name|TokenStream
import|;
end_import
begin_comment
comment|// for javadocs
end_comment
begin_comment
comment|/**  * An AttributeSource contains a list of different {@link AttributeImpl}s,  * and methods to add and get them. There can only be a single instance  * of an attribute in the same AttributeSource instance. This is ensured  * by passing in the actual type of the Attribute (Class&lt;Attribute&gt;) to   * the {@link #addAttribute(Class)}, which then checks if an instance of  * that type is already present. If yes, it returns the instance, otherwise  * it creates a new instance and returns it.  *   *<p><font color="#FF0000">  * WARNING: The status of the new TokenStream, AttributeSource and Attributes is experimental.   * The APIs introduced in these classes with Lucene 2.9 might change in the future.   * We will make our best efforts to keep the APIs backwards-compatible.</font>  */
end_comment
begin_class
DECL|class|AttributeSource
specifier|public
class|class
name|AttributeSource
block|{
comment|/**    * An AttributeFactory creates instances of {@link AttributeImpl}s.    */
DECL|class|AttributeFactory
specifier|public
specifier|static
specifier|abstract
class|class
name|AttributeFactory
block|{
comment|/**      * returns an {@link AttributeImpl} for the supplied {@link Attribute} interface class.      */
DECL|method|createAttributeInstance
specifier|public
specifier|abstract
name|AttributeImpl
name|createAttributeInstance
parameter_list|(
name|Class
name|attClass
parameter_list|)
function_decl|;
comment|/**      * This is the default factory that creates {@link AttributeImpl}s using the      * class name of the supplied {@link Attribute} interface class by appending<code>Impl</code> to it.      */
DECL|field|DEFAULT_ATTRIBUTE_FACTORY
specifier|public
specifier|static
specifier|final
name|AttributeFactory
name|DEFAULT_ATTRIBUTE_FACTORY
init|=
operator|new
name|DefaultAttributeFactory
argument_list|()
decl_stmt|;
DECL|class|DefaultAttributeFactory
specifier|private
specifier|static
specifier|final
class|class
name|DefaultAttributeFactory
extends|extends
name|AttributeFactory
block|{
DECL|field|attClassImplMap
specifier|private
specifier|static
specifier|final
name|IdentityHashMap
comment|/*<Class<? extends Attribute>,Class<? extends AttributeImpl>>*/
name|attClassImplMap
init|=
operator|new
name|IdentityHashMap
argument_list|()
decl_stmt|;
DECL|method|DefaultAttributeFactory
specifier|private
name|DefaultAttributeFactory
parameter_list|()
block|{}
DECL|method|createAttributeInstance
specifier|public
name|AttributeImpl
name|createAttributeInstance
parameter_list|(
name|Class
name|attClass
parameter_list|)
block|{
try|try
block|{
return|return
operator|(
name|AttributeImpl
operator|)
name|getClassForInterface
argument_list|(
name|attClass
argument_list|)
operator|.
name|newInstance
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Could not instantiate class "
operator|+
name|attClass
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Could not instantiate class "
operator|+
name|attClass
argument_list|)
throw|;
block|}
block|}
DECL|method|getClassForInterface
specifier|private
specifier|static
name|Class
name|getClassForInterface
parameter_list|(
name|Class
name|attClass
parameter_list|)
block|{
synchronized|synchronized
init|(
name|attClassImplMap
init|)
block|{
name|Class
name|clazz
init|=
operator|(
name|Class
operator|)
name|attClassImplMap
operator|.
name|get
argument_list|(
name|attClass
argument_list|)
decl_stmt|;
if|if
condition|(
name|clazz
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|attClassImplMap
operator|.
name|put
argument_list|(
name|attClass
argument_list|,
name|clazz
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|attClass
operator|.
name|getName
argument_list|()
operator|+
literal|"Impl"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Could not find implementing class for "
operator|+
name|attClass
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
return|return
name|clazz
return|;
block|}
block|}
block|}
block|}
comment|// These two maps must always be in sync!!!
comment|// So they are private, final and read-only from the outside (read-only iterators)
DECL|field|attributes
specifier|private
specifier|final
name|Map
comment|/*<Class<Attribute>,AttributeImpl>*/
name|attributes
decl_stmt|;
DECL|field|attributeImpls
specifier|private
specifier|final
name|Map
comment|/*<Class<AttributeImpl>,AttributeImpl>*/
name|attributeImpls
decl_stmt|;
DECL|field|factory
specifier|private
name|AttributeFactory
name|factory
decl_stmt|;
comment|/**    * An AttributeSource using the default attribute factory {@link AttributeFactory#DEFAULT_ATTRIBUTE_FACTORY}.    */
DECL|method|AttributeSource
specifier|public
name|AttributeSource
parameter_list|()
block|{
name|this
argument_list|(
name|AttributeFactory
operator|.
name|DEFAULT_ATTRIBUTE_FACTORY
argument_list|)
expr_stmt|;
block|}
comment|/**    * An AttributeSource that uses the same attributes as the supplied one.    */
DECL|method|AttributeSource
specifier|public
name|AttributeSource
parameter_list|(
name|AttributeSource
name|input
parameter_list|)
block|{
if|if
condition|(
name|input
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"input AttributeSource must not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|attributes
operator|=
name|input
operator|.
name|attributes
expr_stmt|;
name|this
operator|.
name|attributeImpls
operator|=
name|input
operator|.
name|attributeImpls
expr_stmt|;
name|this
operator|.
name|factory
operator|=
name|input
operator|.
name|factory
expr_stmt|;
block|}
comment|/**    * An AttributeSource using the supplied {@link AttributeFactory} for creating new {@link Attribute} instances.    */
DECL|method|AttributeSource
specifier|public
name|AttributeSource
parameter_list|(
name|AttributeFactory
name|factory
parameter_list|)
block|{
name|this
operator|.
name|attributes
operator|=
operator|new
name|LinkedHashMap
argument_list|()
expr_stmt|;
name|this
operator|.
name|attributeImpls
operator|=
operator|new
name|LinkedHashMap
argument_list|()
expr_stmt|;
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
block|}
comment|/**    * returns the used AttributeFactory.    */
DECL|method|getAttributeFactory
specifier|public
name|AttributeFactory
name|getAttributeFactory
parameter_list|()
block|{
return|return
name|this
operator|.
name|factory
return|;
block|}
comment|/** Returns a new iterator that iterates the attribute classes    * in the same order they were added in.    */
DECL|method|getAttributeClassesIterator
specifier|public
name|Iterator
comment|/*<Class<? extends Attribute>>*/
name|getAttributeClassesIterator
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|attributes
operator|.
name|keySet
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/** Returns a new iterator that iterates all unique Attribute implementations.    * This iterator may contain less entries that {@link #getAttributeClassesIterator},    * if one instance implements more than one Attribute interface.    */
DECL|method|getAttributeImplsIterator
specifier|public
name|Iterator
comment|/*<AttributeImpl>*/
name|getAttributeImplsIterator
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableCollection
argument_list|(
name|attributeImpls
operator|.
name|values
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/** a cache that stores all interfaces for known implementation classes for performance (slow reflection) */
DECL|field|knownImplClasses
specifier|private
specifier|static
specifier|final
name|IdentityHashMap
comment|/*<Class<? extends AttributeImpl>,LinkedList<Class<? extends Attribute>>>*/
name|knownImplClasses
init|=
operator|new
name|IdentityHashMap
argument_list|()
decl_stmt|;
comment|/** Adds a custom AttributeImpl instance with one or more Attribute interfaces. */
DECL|method|addAttributeImpl
specifier|public
name|void
name|addAttributeImpl
parameter_list|(
specifier|final
name|AttributeImpl
name|att
parameter_list|)
block|{
specifier|final
name|Class
name|clazz
init|=
name|att
operator|.
name|getClass
argument_list|()
decl_stmt|;
if|if
condition|(
name|attributeImpls
operator|.
name|containsKey
argument_list|(
name|clazz
argument_list|)
condition|)
return|return;
name|LinkedList
name|foundInterfaces
decl_stmt|;
synchronized|synchronized
init|(
name|knownImplClasses
init|)
block|{
name|foundInterfaces
operator|=
operator|(
name|LinkedList
operator|)
name|knownImplClasses
operator|.
name|get
argument_list|(
name|clazz
argument_list|)
expr_stmt|;
if|if
condition|(
name|foundInterfaces
operator|==
literal|null
condition|)
block|{
name|knownImplClasses
operator|.
name|put
argument_list|(
name|clazz
argument_list|,
name|foundInterfaces
operator|=
operator|new
name|LinkedList
argument_list|()
argument_list|)
expr_stmt|;
comment|// find all interfaces that this attribute instance implements
comment|// and that extend the Attribute interface
name|Class
name|actClazz
init|=
name|clazz
decl_stmt|;
do|do
block|{
name|Class
index|[]
name|interfaces
init|=
name|actClazz
operator|.
name|getInterfaces
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
name|interfaces
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Class
name|curInterface
init|=
name|interfaces
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|Attribute
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|curInterface
argument_list|)
condition|)
block|{
name|foundInterfaces
operator|.
name|add
argument_list|(
name|curInterface
argument_list|)
expr_stmt|;
block|}
block|}
name|actClazz
operator|=
name|actClazz
operator|.
name|getSuperclass
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|actClazz
operator|!=
literal|null
condition|)
do|;
block|}
block|}
comment|// add all interfaces of this AttributeImpl to the maps
for|for
control|(
name|Iterator
name|it
init|=
name|foundInterfaces
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|Class
name|curInterface
init|=
operator|(
name|Class
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
comment|// Attribute is a superclass of this interface
if|if
condition|(
operator|!
name|attributes
operator|.
name|containsKey
argument_list|(
name|curInterface
argument_list|)
condition|)
block|{
comment|// invalidate state to force recomputation in captureState()
name|this
operator|.
name|currentState
operator|=
literal|null
expr_stmt|;
name|attributes
operator|.
name|put
argument_list|(
name|curInterface
argument_list|,
name|att
argument_list|)
expr_stmt|;
name|attributeImpls
operator|.
name|put
argument_list|(
name|clazz
argument_list|,
name|att
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * The caller must pass in a Class&lt;? extends Attribute&gt; value.    * This method first checks if an instance of that class is     * already in this AttributeSource and returns it. Otherwise a    * new instance is created, added to this AttributeSource and returned.     */
DECL|method|addAttribute
specifier|public
name|AttributeImpl
name|addAttribute
parameter_list|(
name|Class
name|attClass
parameter_list|)
block|{
name|AttributeImpl
name|att
init|=
operator|(
name|AttributeImpl
operator|)
name|attributes
operator|.
name|get
argument_list|(
name|attClass
argument_list|)
decl_stmt|;
if|if
condition|(
name|att
operator|==
literal|null
condition|)
block|{
name|att
operator|=
name|this
operator|.
name|factory
operator|.
name|createAttributeInstance
argument_list|(
name|attClass
argument_list|)
expr_stmt|;
name|addAttributeImpl
argument_list|(
name|att
argument_list|)
expr_stmt|;
block|}
return|return
name|att
return|;
block|}
comment|/** Returns true, iff this AttributeSource has any attributes */
DECL|method|hasAttributes
specifier|public
name|boolean
name|hasAttributes
parameter_list|()
block|{
return|return
operator|!
name|this
operator|.
name|attributes
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|/**    * The caller must pass in a Class&lt;? extends Attribute&gt; value.     * Returns true, iff this AttributeSource contains the passed-in Attribute.    */
DECL|method|hasAttribute
specifier|public
name|boolean
name|hasAttribute
parameter_list|(
name|Class
name|attClass
parameter_list|)
block|{
return|return
name|this
operator|.
name|attributes
operator|.
name|containsKey
argument_list|(
name|attClass
argument_list|)
return|;
block|}
comment|/**    * The caller must pass in a Class&lt;? extends Attribute&gt; value.     * Returns the instance of the passed in Attribute contained in this AttributeSource    *     * @throws IllegalArgumentException if this AttributeSource does not contain the    *         Attribute    */
DECL|method|getAttribute
specifier|public
name|AttributeImpl
name|getAttribute
parameter_list|(
name|Class
name|attClass
parameter_list|)
block|{
name|AttributeImpl
name|att
init|=
operator|(
name|AttributeImpl
operator|)
name|this
operator|.
name|attributes
operator|.
name|get
argument_list|(
name|attClass
argument_list|)
decl_stmt|;
if|if
condition|(
name|att
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"This AttributeSource does not have the attribute '"
operator|+
name|attClass
operator|+
literal|"'."
argument_list|)
throw|;
block|}
return|return
name|att
return|;
block|}
comment|/**    * Resets all Attributes in this AttributeSource by calling    * {@link AttributeImpl#clear()} on each Attribute implementation.    */
DECL|method|clearAttributes
specifier|public
name|void
name|clearAttributes
parameter_list|()
block|{
name|Iterator
name|it
init|=
name|getAttributeImplsIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
operator|(
operator|(
name|AttributeImpl
operator|)
name|it
operator|.
name|next
argument_list|()
operator|)
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * This class holds the state of an AttributeSource.    * @see #captureState    * @see #restoreState    */
DECL|class|State
specifier|public
specifier|static
specifier|final
class|class
name|State
implements|implements
name|Cloneable
block|{
DECL|field|attribute
specifier|private
name|AttributeImpl
name|attribute
decl_stmt|;
DECL|field|next
specifier|private
name|State
name|next
decl_stmt|;
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|State
name|clone
init|=
operator|new
name|State
argument_list|()
decl_stmt|;
name|clone
operator|.
name|attribute
operator|=
operator|(
name|AttributeImpl
operator|)
name|attribute
operator|.
name|clone
argument_list|()
expr_stmt|;
if|if
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|clone
operator|.
name|next
operator|=
operator|(
name|State
operator|)
name|next
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
return|return
name|clone
return|;
block|}
block|}
DECL|field|currentState
specifier|private
name|State
name|currentState
init|=
literal|null
decl_stmt|;
DECL|method|computeCurrentState
specifier|private
name|void
name|computeCurrentState
parameter_list|()
block|{
name|currentState
operator|=
operator|new
name|State
argument_list|()
expr_stmt|;
name|State
name|c
init|=
name|currentState
decl_stmt|;
name|Iterator
name|it
init|=
name|getAttributeImplsIterator
argument_list|()
decl_stmt|;
name|c
operator|.
name|attribute
operator|=
operator|(
name|AttributeImpl
operator|)
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|c
operator|.
name|next
operator|=
operator|new
name|State
argument_list|()
expr_stmt|;
name|c
operator|=
name|c
operator|.
name|next
expr_stmt|;
name|c
operator|.
name|attribute
operator|=
operator|(
name|AttributeImpl
operator|)
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Captures the state of all Attributes. The return value can be passed to    * {@link #restoreState} to restore the state of this or another AttributeSource.    */
DECL|method|captureState
specifier|public
name|State
name|captureState
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hasAttributes
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|currentState
operator|==
literal|null
condition|)
block|{
name|computeCurrentState
argument_list|()
expr_stmt|;
block|}
return|return
operator|(
name|State
operator|)
name|this
operator|.
name|currentState
operator|.
name|clone
argument_list|()
return|;
block|}
comment|/**    * Restores this state by copying the values of all attribute implementations    * that this state contains into the attributes implementations of the targetStream.    * The targetStream must contain a corresponding instance for each argument    * contained in this state (e.g. it is not possible to restore the state of    * an AttributeSource containing a TermAttribute into a AttributeSource using    * a Token instance as implementation).    *<p>    * Note that this method does not affect attributes of the targetStream    * that are not contained in this state. In other words, if for example    * the targetStream contains an OffsetAttribute, but this state doesn't, then    * the value of the OffsetAttribute remains unchanged. It might be desirable to    * reset its value to the default, in which case the caller should first    * call {@link TokenStream#clearAttributes()} on the targetStream.       */
DECL|method|restoreState
specifier|public
name|void
name|restoreState
parameter_list|(
name|State
name|state
parameter_list|)
block|{
if|if
condition|(
name|state
operator|==
literal|null
condition|)
return|return;
do|do
block|{
name|AttributeImpl
name|targetImpl
init|=
operator|(
name|AttributeImpl
operator|)
name|attributeImpls
operator|.
name|get
argument_list|(
name|state
operator|.
name|attribute
operator|.
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|targetImpl
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"State contains an AttributeImpl that is not in this AttributeSource"
argument_list|)
throw|;
name|state
operator|.
name|attribute
operator|.
name|copyTo
argument_list|(
name|targetImpl
argument_list|)
expr_stmt|;
name|state
operator|=
name|state
operator|.
name|next
expr_stmt|;
block|}
do|while
condition|(
name|state
operator|!=
literal|null
condition|)
do|;
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|code
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|hasAttributes
argument_list|()
condition|)
block|{
name|Iterator
name|it
init|=
name|getAttributeImplsIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|code
operator|=
name|code
operator|*
literal|31
operator|+
name|it
operator|.
name|next
argument_list|()
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|code
return|;
block|}
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|instanceof
name|AttributeSource
condition|)
block|{
name|AttributeSource
name|other
init|=
operator|(
name|AttributeSource
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|hasAttributes
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|other
operator|.
name|hasAttributes
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|this
operator|.
name|attributeImpls
operator|.
name|size
argument_list|()
operator|!=
name|other
operator|.
name|attributeImpls
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// it is only equal if all attribute impls are the same in the same order
name|Iterator
name|thisIt
init|=
name|this
operator|.
name|getAttributeImplsIterator
argument_list|()
decl_stmt|;
name|Iterator
name|otherIt
init|=
name|other
operator|.
name|getAttributeImplsIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|thisIt
operator|.
name|hasNext
argument_list|()
operator|&&
name|otherIt
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|AttributeImpl
name|thisAtt
init|=
operator|(
name|AttributeImpl
operator|)
name|thisIt
operator|.
name|next
argument_list|()
decl_stmt|;
name|AttributeImpl
name|otherAtt
init|=
operator|(
name|AttributeImpl
operator|)
name|otherIt
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|otherAtt
operator|.
name|getClass
argument_list|()
operator|!=
name|thisAtt
operator|.
name|getClass
argument_list|()
operator|||
operator|!
name|otherAtt
operator|.
name|equals
argument_list|(
name|thisAtt
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
operator|!
name|other
operator|.
name|hasAttributes
argument_list|()
return|;
block|}
block|}
else|else
return|return
literal|false
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasAttributes
argument_list|()
condition|)
block|{
name|Iterator
name|it
init|=
name|getAttributeImplsIterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|it
operator|.
name|next
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|it
operator|.
name|next
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|sb
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Performs a clone of all {@link AttributeImpl} instances returned in a new    * AttributeSource instance. This method can be used to e.g. create another TokenStream    * with exactly the same attributes (using {@link #AttributeSource(AttributeSource)})    */
DECL|method|cloneAttributes
specifier|public
name|AttributeSource
name|cloneAttributes
parameter_list|()
block|{
name|AttributeSource
name|clone
init|=
operator|new
name|AttributeSource
argument_list|(
name|this
operator|.
name|factory
argument_list|)
decl_stmt|;
comment|// first clone the impls
name|Iterator
comment|/*<AttributeImpl>*/
name|implIt
init|=
name|getAttributeImplsIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|implIt
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|AttributeImpl
name|impl
init|=
operator|(
name|AttributeImpl
operator|)
name|implIt
operator|.
name|next
argument_list|()
decl_stmt|;
name|clone
operator|.
name|attributeImpls
operator|.
name|put
argument_list|(
name|impl
operator|.
name|getClass
argument_list|()
argument_list|,
name|impl
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// now the interfaces
name|Iterator
comment|/*<Entry<Class<Attribute>, AttributeImpl>>*/
name|attIt
init|=
name|this
operator|.
name|attributes
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|attIt
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Entry
comment|/*<Class<Attribute>, AttributeImpl>*/
name|entry
init|=
operator|(
name|Entry
comment|/*<Class<Attribute>, AttributeImpl>*/
operator|)
name|attIt
operator|.
name|next
argument_list|()
decl_stmt|;
name|clone
operator|.
name|attributes
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|clone
operator|.
name|attributeImpls
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|clone
return|;
block|}
block|}
end_class
end_unit
