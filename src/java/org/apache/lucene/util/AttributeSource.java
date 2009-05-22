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
name|LinkedHashMap
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
comment|/**  * An AttributeSource contains a list of different {@link Attribute}s,  * and methods to add and get them. There can only be a single instance  * of an attribute in the same AttributeSource instance. This is ensured  * by passing in the actual type of the Attribute (Class&lt;Attribute&gt;) to   * the {@link #addAttribute(Class)}, which then checks if an instance of  * that type is already present. If yes, it returns the instance, otherwise  * it creates a new instance and returns it.  *   *<p><font color="#FF0000">  * WARNING: The status of the new TokenStream, AttributeSource and Attributes is experimental.   * The APIs introduced in these classes with Lucene 2.9 might change in the future.   * We will make our best efforts to keep the APIs backwards-compatible.</font>  */
end_comment
begin_class
DECL|class|AttributeSource
specifier|public
class|class
name|AttributeSource
block|{
comment|/**    * An AttributeAcceptor defines only a single method {@link #accept(Class)}.    * It can be used for e. g. buffering purposes to specify which attributes    * to buffer.     */
DECL|class|AttributeAcceptor
specifier|public
specifier|static
specifier|abstract
class|class
name|AttributeAcceptor
block|{
comment|/** Return true, to accept this attribute; false otherwise */
DECL|method|accept
specifier|public
specifier|abstract
name|boolean
name|accept
parameter_list|(
name|Class
name|attClass
parameter_list|)
function_decl|;
block|}
comment|/**    * Default AttributeAcceptor that accepts all attributes.    */
DECL|field|AllAcceptor
specifier|public
specifier|static
specifier|final
name|AttributeAcceptor
name|AllAcceptor
init|=
operator|new
name|AttributeAcceptor
argument_list|()
block|{
specifier|public
name|boolean
name|accept
parameter_list|(
name|Class
name|attClass
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
comment|/**    * Holds the Class&lt;Attribute&gt; -> Attribute mapping    */
DECL|field|attributes
specifier|protected
name|Map
name|attributes
decl_stmt|;
DECL|method|AttributeSource
specifier|public
name|AttributeSource
parameter_list|()
block|{
name|this
operator|.
name|attributes
operator|=
operator|new
name|LinkedHashMap
argument_list|()
expr_stmt|;
block|}
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
block|}
comment|/** Returns an iterator that iterates the attributes     * in the same order they were added in.    */
DECL|method|getAttributesIterator
specifier|public
name|Iterator
name|getAttributesIterator
parameter_list|()
block|{
return|return
name|attributes
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/**    * The caller must pass in a Class&lt;? extends Attribute&gt; value.    * This method first checks if an instance of that class is     * already in this AttributeSource and returns it. Otherwise a    * new instance is created, added to this AttributeSource and returned.     */
DECL|method|addAttribute
specifier|public
name|Attribute
name|addAttribute
parameter_list|(
name|Class
name|attClass
parameter_list|)
block|{
name|Attribute
name|att
init|=
operator|(
name|Attribute
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
try|try
block|{
name|att
operator|=
operator|(
name|Attribute
operator|)
name|attClass
operator|.
name|newInstance
argument_list|()
expr_stmt|;
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
name|attributes
operator|.
name|put
argument_list|(
name|attClass
argument_list|,
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
name|Attribute
name|getAttribute
parameter_list|(
name|Class
name|attClass
parameter_list|)
block|{
name|Attribute
name|att
init|=
operator|(
name|Attribute
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
literal|"This token does not have the attribute '"
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
comment|/**    * Resets all Attributes in this AttributeSource by calling    * {@link Attribute#clear()} on each Attribute.    */
DECL|method|clearAttributes
specifier|public
name|void
name|clearAttributes
parameter_list|()
block|{
name|Iterator
name|it
init|=
name|getAttributesIterator
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
name|Attribute
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
comment|/**    * Captures the current state of the passed in TokenStream.    *<p>    * This state will contain all of the passed in TokenStream's    * {@link Attribute}s. If only a subset of the attributes is needed    * please use {@link #captureState(AttributeAcceptor)}     */
DECL|method|captureState
specifier|public
name|AttributeSource
name|captureState
parameter_list|()
block|{
return|return
name|captureState
argument_list|(
name|AllAcceptor
argument_list|)
return|;
block|}
comment|/**    * Captures the current state of the passed in TokenStream.    *<p>    * This state will contain all of the passed in TokenStream's    * {@link Attribute}s which the {@link AttributeAcceptor} accepts.     */
DECL|method|captureState
specifier|public
name|AttributeSource
name|captureState
parameter_list|(
name|AttributeAcceptor
name|acceptor
parameter_list|)
block|{
name|AttributeSource
name|state
init|=
operator|new
name|AttributeSource
argument_list|()
decl_stmt|;
name|Iterator
name|it
init|=
name|getAttributesIterator
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
name|Attribute
name|att
init|=
operator|(
name|Attribute
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|acceptor
operator|.
name|accept
argument_list|(
name|att
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
name|Attribute
name|clone
init|=
operator|(
name|Attribute
operator|)
name|att
operator|.
name|clone
argument_list|()
decl_stmt|;
name|state
operator|.
name|attributes
operator|.
name|put
argument_list|(
name|att
operator|.
name|getClass
argument_list|()
argument_list|,
name|clone
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|state
return|;
block|}
comment|/**    * Restores this state by copying the values of all attributes     * that this state contains into the attributes of the targetStream.    * The targetStream must contain a corresponding instance for each argument    * contained in this state.    *<p>    * Note that this method does not affect attributes of the targetStream    * that are not contained in this state. In other words, if for example    * the targetStream contains an OffsetAttribute, but this state doesn't, then    * the value of the OffsetAttribute remains unchanged. It might be desirable to    * reset its value to the default, in which case the caller should first    * call {@link TokenStream#clearAttributes()} on the targetStream.       */
DECL|method|restoreState
specifier|public
name|void
name|restoreState
parameter_list|(
name|AttributeSource
name|target
parameter_list|)
block|{
name|Iterator
name|it
init|=
name|getAttributesIterator
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
name|Attribute
name|att
init|=
operator|(
name|Attribute
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|Attribute
name|targetAtt
init|=
name|target
operator|.
name|getAttribute
argument_list|(
name|att
operator|.
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
name|att
operator|.
name|copyTo
argument_list|(
name|targetAtt
argument_list|)
expr_stmt|;
block|}
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
name|getAttributesIterator
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
name|attributes
operator|.
name|size
argument_list|()
operator|!=
name|other
operator|.
name|attributes
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Iterator
name|it
init|=
name|getAttributesIterator
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
name|Class
name|attName
init|=
name|it
operator|.
name|next
argument_list|()
operator|.
name|getClass
argument_list|()
decl_stmt|;
name|Attribute
name|otherAtt
init|=
operator|(
name|Attribute
operator|)
name|other
operator|.
name|attributes
operator|.
name|get
argument_list|(
name|attName
argument_list|)
decl_stmt|;
if|if
condition|(
name|otherAtt
operator|==
literal|null
operator|||
operator|!
name|otherAtt
operator|.
name|equals
argument_list|(
name|attributes
operator|.
name|get
argument_list|(
name|attName
argument_list|)
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
comment|// TODO: Java 1.5
comment|//  private Map<Class<? extends Attribute>, Attribute> attributes;
comment|//  public<T extends Attribute> T addAttribute(Class<T> attClass) {
comment|//    T att = (T) attributes.get(attClass);
comment|//    if (att == null) {
comment|//      try {
comment|//        att = attClass.newInstance();
comment|//      } catch (InstantiationException e) {
comment|//        throw new IllegalArgumentException("Could not instantiate class " + attClass);
comment|//      } catch (IllegalAccessException e) {
comment|//        throw new IllegalArgumentException("Could not instantiate class " + attClass);
comment|//      }
comment|//
comment|//      attributes.put(attClass, att);
comment|//    }
comment|//    return att;
comment|//  }
comment|//
comment|//  public boolean hasAttribute(Class<? extends Attribute> attClass) {
comment|//    return this.attributes.containsKey(attClass);
comment|//  }
comment|//
comment|//  public<T extends Attribute> T getAttribute(Class<T> attClass) {
comment|//    Attribute att = this.attributes.get(attClass);
comment|//    if (att == null) {
comment|//      throw new IllegalArgumentException("This token does not have the attribute '" + attClass + "'.");
comment|//    }
comment|//
comment|//    return (T) att;
comment|//  }
comment|//
block|}
end_class
end_unit
