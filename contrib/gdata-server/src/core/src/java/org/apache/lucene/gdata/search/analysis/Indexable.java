begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.gdata.search.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|search
operator|.
name|analysis
package|;
end_package
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathExpressionException
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
name|data
operator|.
name|ServerBaseEntry
import|;
end_import
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import
begin_comment
comment|/**  * This class wraps the access to the GData entities to access them via xpath  * expressions. An arbitrary valid Xpath expression can be passed to the  *<tt>applyPath</tt> method to access an element, attribute etc. in the gdata  * entity.  *   *  * @param<R> -  *            a subtype of {@link org.w3c.dom.Node} returned by the applyPath  *            method  * @param<I> -  *            a subtype of {@link org.apache.lucene.gdata.data.ServerBaseEntry}  */
end_comment
begin_class
DECL|class|Indexable
specifier|public
specifier|abstract
class|class
name|Indexable
parameter_list|<
name|R
extends|extends
name|Node
parameter_list|,
name|I
extends|extends
name|ServerBaseEntry
parameter_list|>
block|{
DECL|field|applyAble
specifier|protected
name|ServerBaseEntry
name|applyAble
decl_stmt|;
comment|/**      * @param applyAble      */
DECL|method|Indexable
name|Indexable
parameter_list|(
name|I
name|applyAble
parameter_list|)
block|{
name|this
operator|.
name|applyAble
operator|=
name|applyAble
expr_stmt|;
block|}
comment|/**      * @param xPath -      *            a valid xpath expression      * @return - the requested element<b>R</b>      * @throws XPathExpressionException      */
DECL|method|applyPath
specifier|public
specifier|abstract
name|R
name|applyPath
parameter_list|(
name|String
name|xPath
parameter_list|)
throws|throws
name|XPathExpressionException
function_decl|;
comment|/**      * Factory method to create new<tt>Indexable</tt> instances.      *       * @param<R> -      *            a subtype of {@link org.w3c.dom.Node} returned by the      *            applyPath method      * @param<I> -      *            a subtype of      *            {@link org.apache.lucene.gdata.data.ServerBaseEntry}      * @param entry -      *            the entry to wrap in a<tt>Indexable</tt>      * @return - a new instance of<tt>Indexable</tt> to access the entry via      *         Xpath      * @throws NotIndexableException - if<b>I<b> can not be parsed.       */
DECL|method|getIndexable
specifier|public
specifier|static
parameter_list|<
name|R
extends|extends
name|Node
parameter_list|,
name|I
extends|extends
name|ServerBaseEntry
parameter_list|>
name|Indexable
argument_list|<
name|R
argument_list|,
name|I
argument_list|>
name|getIndexable
parameter_list|(
name|I
name|entry
parameter_list|)
throws|throws
name|NotIndexableException
block|{
return|return
operator|new
name|DomIndexable
argument_list|<
name|R
argument_list|,
name|I
argument_list|>
argument_list|(
name|entry
argument_list|)
return|;
block|}
block|}
end_class
end_unit
