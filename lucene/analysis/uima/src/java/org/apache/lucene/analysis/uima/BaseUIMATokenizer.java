begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.uima
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|uima
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Tokenizer
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
name|uima
operator|.
name|ae
operator|.
name|AEProviderFactory
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|analysis_engine
operator|.
name|AnalysisEngine
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|analysis_engine
operator|.
name|AnalysisEngineProcessException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|cas
operator|.
name|CAS
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|cas
operator|.
name|FSIterator
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|cas
operator|.
name|text
operator|.
name|AnnotationFS
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|resource
operator|.
name|ResourceInitializationException
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
name|util
operator|.
name|Map
import|;
end_import
begin_comment
comment|/**  * Abstract base implementation of a {@link Tokenizer} which is able to analyze the given input with a  * UIMA {@link AnalysisEngine}  */
end_comment
begin_class
DECL|class|BaseUIMATokenizer
specifier|public
specifier|abstract
class|class
name|BaseUIMATokenizer
extends|extends
name|Tokenizer
block|{
DECL|field|iterator
specifier|protected
name|FSIterator
argument_list|<
name|AnnotationFS
argument_list|>
name|iterator
decl_stmt|;
DECL|field|descriptorPath
specifier|private
specifier|final
name|String
name|descriptorPath
decl_stmt|;
DECL|field|configurationParameters
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|configurationParameters
decl_stmt|;
DECL|field|ae
specifier|protected
name|AnalysisEngine
name|ae
decl_stmt|;
DECL|field|cas
specifier|protected
name|CAS
name|cas
decl_stmt|;
DECL|method|BaseUIMATokenizer
specifier|protected
name|BaseUIMATokenizer
parameter_list|(
name|Reader
name|reader
parameter_list|,
name|String
name|descriptorPath
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|configurationParameters
parameter_list|)
block|{
name|super
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|this
operator|.
name|descriptorPath
operator|=
name|descriptorPath
expr_stmt|;
name|this
operator|.
name|configurationParameters
operator|=
name|configurationParameters
expr_stmt|;
block|}
comment|/**    * analyzes the tokenizer input using the given analysis engine    *<p/>    * {@link #cas} will be filled with  extracted metadata (UIMA annotations, feature structures)    *    * @throws IOException If there is a low-level I/O error.    */
DECL|method|analyzeInput
specifier|protected
name|void
name|analyzeInput
parameter_list|()
throws|throws
name|ResourceInitializationException
throws|,
name|AnalysisEngineProcessException
throws|,
name|IOException
block|{
if|if
condition|(
name|ae
operator|==
literal|null
condition|)
block|{
name|ae
operator|=
name|AEProviderFactory
operator|.
name|getInstance
argument_list|()
operator|.
name|getAEProvider
argument_list|(
literal|null
argument_list|,
name|descriptorPath
argument_list|,
name|configurationParameters
argument_list|)
operator|.
name|getAE
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|cas
operator|==
literal|null
condition|)
block|{
name|cas
operator|=
name|ae
operator|.
name|newCAS
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|cas
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
name|cas
operator|.
name|setDocumentText
argument_list|(
name|toString
argument_list|(
name|input
argument_list|)
argument_list|)
expr_stmt|;
name|ae
operator|.
name|process
argument_list|(
name|cas
argument_list|)
expr_stmt|;
block|}
comment|/**    * initialize the FSIterator which is used to build tokens at each incrementToken() method call    *    * @throws IOException If there is a low-level I/O error.    */
DECL|method|initializeIterator
specifier|protected
specifier|abstract
name|void
name|initializeIterator
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|toString
specifier|private
name|String
name|toString
parameter_list|(
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|StringBuilder
name|stringBuilder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|ch
decl_stmt|;
while|while
condition|(
operator|(
name|ch
operator|=
name|reader
operator|.
name|read
argument_list|()
operator|)
operator|>
operator|-
literal|1
condition|)
block|{
name|stringBuilder
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|ch
argument_list|)
expr_stmt|;
block|}
return|return
name|stringBuilder
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|iterator
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|end
specifier|public
name|void
name|end
parameter_list|()
throws|throws
name|IOException
block|{
name|iterator
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class
end_unit
