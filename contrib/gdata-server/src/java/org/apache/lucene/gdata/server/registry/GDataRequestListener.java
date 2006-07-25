begin_unit
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.gdata.server.registry
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
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletRequestEvent
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletRequestListener
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|server
operator|.
name|registry
operator|.
name|Scope
operator|.
name|ScopeType
import|;
end_import
begin_comment
comment|/**  * This<tt>ServletRequestListener</tt> is used by the registry to notify  * registered {@link org.apache.lucene.gdata.server.registry.ScopeVisitor}  * implementations when a request is initialized e.g destroyed.  *   *   * @see org.apache.lucene.gdata.server.registry.ScopeVisitable  * @see javax.servlet.ServletRequestListener  * @author Simon Willnauer  *   */
end_comment
begin_class
annotation|@
name|Scope
argument_list|(
name|scope
operator|=
name|ScopeType
operator|.
name|REQUEST
argument_list|)
DECL|class|GDataRequestListener
specifier|public
class|class
name|GDataRequestListener
implements|implements
name|ServletRequestListener
implements|,
name|ScopeVisitable
block|{
DECL|field|registry
specifier|private
specifier|final
name|GDataServerRegistry
name|registry
decl_stmt|;
DECL|field|visitors
specifier|private
specifier|final
name|List
argument_list|<
name|ScopeVisitor
argument_list|>
name|visitors
init|=
operator|new
name|ArrayList
argument_list|<
name|ScopeVisitor
argument_list|>
argument_list|(
literal|5
argument_list|)
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|GDataRequestListener
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * @throws RegistryException      *       */
DECL|method|GDataRequestListener
specifier|public
name|GDataRequestListener
parameter_list|()
throws|throws
name|RegistryException
block|{
name|this
operator|.
name|registry
operator|=
name|GDataServerRegistry
operator|.
name|getRegistry
argument_list|()
expr_stmt|;
name|this
operator|.
name|registry
operator|.
name|registerScopeVisitable
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see javax.servlet.ServletRequestListener#requestDestroyed(javax.servlet.ServletRequestEvent)      */
DECL|method|requestDestroyed
specifier|public
name|void
name|requestDestroyed
parameter_list|(
name|ServletRequestEvent
name|arg0
parameter_list|)
block|{
for|for
control|(
name|ScopeVisitor
name|visitor
range|:
name|this
operator|.
name|visitors
control|)
block|{
name|visitor
operator|.
name|visiteDestroy
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @see javax.servlet.ServletRequestListener#requestInitialized(javax.servlet.ServletRequestEvent)      */
DECL|method|requestInitialized
specifier|public
name|void
name|requestInitialized
parameter_list|(
name|ServletRequestEvent
name|arg0
parameter_list|)
block|{
for|for
control|(
name|ScopeVisitor
name|visitor
range|:
name|this
operator|.
name|visitors
control|)
block|{
name|visitor
operator|.
name|visiteInitialize
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @see org.apache.lucene.gdata.server.registry.ScopeVisitable#accept(org.apache.lucene.gdata.server.registry.ScopeVisitor)      */
DECL|method|accept
specifier|public
name|void
name|accept
parameter_list|(
name|ScopeVisitor
name|visitor
parameter_list|)
block|{
if|if
condition|(
operator|!
name|this
operator|.
name|visitors
operator|.
name|contains
argument_list|(
name|visitor
argument_list|)
operator|&&
name|visitor
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|visitors
operator|.
name|add
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"visitor added -- "
operator|+
name|visitor
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
