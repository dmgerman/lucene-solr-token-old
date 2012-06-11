begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|phonetic
operator|.
name|DoubleMetaphoneFilter
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
name|util
operator|.
name|TokenFilterFactory
import|;
end_import
begin_comment
comment|/**  * Factory for {@link DoubleMetaphoneFilter}.  *<pre class="prettyprint">  *&lt;fieldType name="text_dblmtphn" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.WhitespaceTokenizerFactory"/&gt;  *&lt;filter class="solr.DoubleMetaphoneFilterFactory" inject="true" maxCodeLength="4"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  *  */
end_comment
begin_class
DECL|class|DoubleMetaphoneFilterFactory
specifier|public
class|class
name|DoubleMetaphoneFilterFactory
extends|extends
name|TokenFilterFactory
block|{
DECL|field|INJECT
specifier|public
specifier|static
specifier|final
name|String
name|INJECT
init|=
literal|"inject"
decl_stmt|;
DECL|field|MAX_CODE_LENGTH
specifier|public
specifier|static
specifier|final
name|String
name|MAX_CODE_LENGTH
init|=
literal|"maxCodeLength"
decl_stmt|;
DECL|field|DEFAULT_MAX_CODE_LENGTH
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_CODE_LENGTH
init|=
literal|4
decl_stmt|;
DECL|field|inject
specifier|private
name|boolean
name|inject
init|=
literal|true
decl_stmt|;
DECL|field|maxCodeLength
specifier|private
name|int
name|maxCodeLength
init|=
name|DEFAULT_MAX_CODE_LENGTH
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|inject
operator|=
name|getBoolean
argument_list|(
name|INJECT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|args
operator|.
name|get
argument_list|(
name|MAX_CODE_LENGTH
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|maxCodeLength
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
operator|.
name|get
argument_list|(
name|MAX_CODE_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|create
specifier|public
name|DoubleMetaphoneFilter
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
return|return
operator|new
name|DoubleMetaphoneFilter
argument_list|(
name|input
argument_list|,
name|maxCodeLength
argument_list|,
name|inject
argument_list|)
return|;
block|}
block|}
end_class
end_unit
