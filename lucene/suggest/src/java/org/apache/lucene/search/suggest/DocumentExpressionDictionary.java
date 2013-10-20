begin_unit
begin_package
DECL|package|org.apache.lucene.search.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
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
name|List
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|NumericDocValuesField
import|;
end_import
begin_comment
comment|// javadocs
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|expressions
operator|.
name|Expression
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
name|expressions
operator|.
name|SimpleBindings
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
name|expressions
operator|.
name|js
operator|.
name|JavascriptCompiler
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
name|index
operator|.
name|AtomicReaderContext
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
name|index
operator|.
name|CompositeReader
import|;
end_import
begin_comment
comment|// javadocs
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
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
name|queries
operator|.
name|function
operator|.
name|FunctionValues
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|search
operator|.
name|SortField
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
name|util
operator|.
name|BytesRefIterator
import|;
end_import
begin_comment
comment|/**  * Dictionary with terms and optionally payload information   * taken from stored fields in a Lucene index. Similar to   * {@link DocumentDictionary}, except it computes the weight  * of the terms in a document based on a user-defined expression  * having one or more {@link NumericDocValuesField} in the document.  *   *<b>NOTE:</b>   *<ul>  *<li>  *      The term and (optionally) payload fields supplied  *      are required for ALL documents and has to be stored  *</li>  *<li>  *      {@link CompositeReader} is not supported.  *</li>  *</ul>  */
end_comment
begin_class
DECL|class|DocumentExpressionDictionary
specifier|public
class|class
name|DocumentExpressionDictionary
extends|extends
name|DocumentDictionary
block|{
DECL|field|weightsValueSource
specifier|private
name|ValueSource
name|weightsValueSource
decl_stmt|;
comment|/**    * Creates a new dictionary with the contents of the fields named<code>field</code>    * for the terms and computes the corresponding weights of the term by compiling the    * user-defined<code>weightExpression</code> using the<code>sortFields</code>    * bindings.    */
DECL|method|DocumentExpressionDictionary
specifier|public
name|DocumentExpressionDictionary
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|String
name|weightExpression
parameter_list|,
name|Set
argument_list|<
name|SortField
argument_list|>
name|sortFields
parameter_list|)
block|{
name|this
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|weightExpression
argument_list|,
name|sortFields
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new dictionary with the contents of the fields named<code>field</code>    * for the terms,<code>payloadField</code> for the corresponding payloads    * and computes the corresponding weights of the term by compiling the    * user-defined<code>weightExpression</code> using the<code>sortFields</code>    * bindings.    */
DECL|method|DocumentExpressionDictionary
specifier|public
name|DocumentExpressionDictionary
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|String
name|weightExpression
parameter_list|,
name|Set
argument_list|<
name|SortField
argument_list|>
name|sortFields
parameter_list|,
name|String
name|payload
parameter_list|)
block|{
name|super
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
literal|null
argument_list|,
name|payload
argument_list|)
expr_stmt|;
name|Expression
name|expression
init|=
literal|null
decl_stmt|;
try|try
block|{
name|expression
operator|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
name|weightExpression
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|()
throw|;
block|}
name|SimpleBindings
name|bindings
init|=
operator|new
name|SimpleBindings
argument_list|()
decl_stmt|;
for|for
control|(
name|SortField
name|sortField
range|:
name|sortFields
control|)
block|{
name|bindings
operator|.
name|add
argument_list|(
name|sortField
argument_list|)
expr_stmt|;
block|}
name|weightsValueSource
operator|=
name|expression
operator|.
name|getValueSource
argument_list|(
name|bindings
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getWordsIterator
specifier|public
name|BytesRefIterator
name|getWordsIterator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|DocumentExpressionInputIterator
argument_list|(
name|payloadField
operator|!=
literal|null
argument_list|)
return|;
block|}
DECL|class|DocumentExpressionInputIterator
specifier|final
class|class
name|DocumentExpressionInputIterator
extends|extends
name|DocumentDictionary
operator|.
name|DocumentInputIterator
block|{
DECL|field|weightValues
specifier|private
name|FunctionValues
name|weightValues
decl_stmt|;
DECL|method|DocumentExpressionInputIterator
specifier|public
name|DocumentExpressionInputIterator
parameter_list|(
name|boolean
name|hasPayloads
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|hasPayloads
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|AtomicReaderContext
argument_list|>
name|leaves
init|=
name|reader
operator|.
name|leaves
argument_list|()
decl_stmt|;
if|if
condition|(
name|leaves
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"CompositeReader is not supported"
argument_list|)
throw|;
block|}
name|weightValues
operator|=
name|weightsValueSource
operator|.
name|getValues
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
argument_list|,
name|leaves
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getWeight
specifier|protected
name|long
name|getWeight
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
name|weightValues
operator|.
name|longVal
argument_list|(
name|docId
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit