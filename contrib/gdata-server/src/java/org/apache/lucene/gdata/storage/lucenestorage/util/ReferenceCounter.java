begin_unit
begin_comment
comment|/**   * Copyright 2004 The Apache Software Foundation   *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   *     http://www.apache.org/licenses/LICENSE-2.0   *   * Unless required by applicable law or agreed to in writing, software   * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   */
end_comment
begin_package
DECL|package|org.apache.lucene.gdata.storage.lucenestorage.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|storage
operator|.
name|lucenestorage
operator|.
name|util
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import
begin_comment
comment|/**   * A reference counting utility. This is use to keep track of released objects   * of<code>Type</code>.   *    * @author Simon Willnauer   * @param<Type> -   *            the type of the object   *    */
end_comment
begin_class
DECL|class|ReferenceCounter
specifier|public
specifier|abstract
class|class
name|ReferenceCounter
parameter_list|<
name|Type
parameter_list|>
block|{
DECL|field|resource
specifier|protected
specifier|final
name|Type
name|resource
decl_stmt|;
DECL|field|refcounter
specifier|private
name|AtomicInteger
name|refcounter
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
comment|/**       * @param resource -       *            the resouce to track       *        */
DECL|method|ReferenceCounter
specifier|public
name|ReferenceCounter
parameter_list|(
name|Type
name|resource
parameter_list|)
block|{
name|this
operator|.
name|resource
operator|=
name|resource
expr_stmt|;
block|}
comment|/**       *        * Decrements the reference. If no references remain the       * {@link ReferenceCounter#close()} method will be inoked;       */
DECL|method|decrementRef
specifier|public
specifier|final
name|void
name|decrementRef
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|refcounter
operator|.
name|decrementAndGet
argument_list|()
operator|==
literal|0
condition|)
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**       * A custom implementation. Performs an action if no reference remaining       *        */
DECL|method|close
specifier|protected
specifier|abstract
name|void
name|close
parameter_list|()
function_decl|;
comment|/**       * Increments the reference       *        * @return the refernece object       */
DECL|method|increamentReference
specifier|public
specifier|final
name|ReferenceCounter
argument_list|<
name|Type
argument_list|>
name|increamentReference
parameter_list|()
block|{
name|this
operator|.
name|refcounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**       * @return - the resource to keep track of       */
DECL|method|get
specifier|public
specifier|final
name|Type
name|get
parameter_list|()
block|{
return|return
name|this
operator|.
name|resource
return|;
block|}
block|}
end_class
end_unit
