begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
package|;
end_package
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
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/**  * @version $Id$  */
end_comment
begin_class
DECL|class|SolrAnalyzer
specifier|public
specifier|abstract
class|class
name|SolrAnalyzer
extends|extends
name|Analyzer
block|{
DECL|field|posIncGap
name|int
name|posIncGap
init|=
literal|0
decl_stmt|;
DECL|method|setPositionIncrementGap
specifier|public
name|void
name|setPositionIncrementGap
parameter_list|(
name|int
name|gap
parameter_list|)
block|{
name|posIncGap
operator|=
name|gap
expr_stmt|;
block|}
DECL|method|getPositionIncrementGap
specifier|public
name|int
name|getPositionIncrementGap
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|posIncGap
return|;
block|}
comment|/** wrap the reader in a CharStream, if appropriate */
DECL|method|charStream
specifier|public
name|Reader
name|charStream
parameter_list|(
name|Reader
name|reader
parameter_list|)
block|{
return|return
name|reader
return|;
block|}
annotation|@
name|Override
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
return|return
name|getStream
argument_list|(
name|fieldName
argument_list|,
name|reader
argument_list|)
operator|.
name|getTokenStream
argument_list|()
return|;
block|}
DECL|class|TokenStreamInfo
specifier|public
specifier|static
class|class
name|TokenStreamInfo
block|{
DECL|field|tokenizer
specifier|private
specifier|final
name|Tokenizer
name|tokenizer
decl_stmt|;
DECL|field|tokenStream
specifier|private
specifier|final
name|TokenStream
name|tokenStream
decl_stmt|;
DECL|method|TokenStreamInfo
specifier|public
name|TokenStreamInfo
parameter_list|(
name|Tokenizer
name|tokenizer
parameter_list|,
name|TokenStream
name|tokenStream
parameter_list|)
block|{
name|this
operator|.
name|tokenizer
operator|=
name|tokenizer
expr_stmt|;
name|this
operator|.
name|tokenStream
operator|=
name|tokenStream
expr_stmt|;
block|}
DECL|method|getTokenizer
specifier|public
name|Tokenizer
name|getTokenizer
parameter_list|()
block|{
return|return
name|tokenizer
return|;
block|}
DECL|method|getTokenStream
specifier|public
name|TokenStream
name|getTokenStream
parameter_list|()
block|{
return|return
name|tokenStream
return|;
block|}
block|}
DECL|method|getStream
specifier|public
specifier|abstract
name|TokenStreamInfo
name|getStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|reusableTokenStream
specifier|public
name|TokenStream
name|reusableTokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
comment|// if (true) return tokenStream(fieldName, reader);
name|TokenStreamInfo
name|tsi
init|=
operator|(
name|TokenStreamInfo
operator|)
name|getPreviousTokenStream
argument_list|()
decl_stmt|;
if|if
condition|(
name|tsi
operator|!=
literal|null
condition|)
block|{
name|tsi
operator|.
name|getTokenizer
argument_list|()
operator|.
name|reset
argument_list|(
name|charStream
argument_list|(
name|reader
argument_list|)
argument_list|)
expr_stmt|;
comment|// the consumer will currently call reset() on the TokenStream to hit all the filters.
comment|// this isn't necessarily guaranteed by the APIs... but is currently done
comment|// by lucene indexing in DocInverterPerField, and in the QueryParser
return|return
name|tsi
operator|.
name|getTokenStream
argument_list|()
return|;
block|}
else|else
block|{
name|tsi
operator|=
name|getStream
argument_list|(
name|fieldName
argument_list|,
name|reader
argument_list|)
expr_stmt|;
name|setPreviousTokenStream
argument_list|(
name|tsi
argument_list|)
expr_stmt|;
return|return
name|tsi
operator|.
name|getTokenStream
argument_list|()
return|;
block|}
block|}
block|}
end_class
end_unit
