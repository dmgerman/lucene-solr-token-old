begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.standard
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|standard
package|;
end_package
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
operator|.
name|Set
import|;
end_import
begin_comment
comment|/**  * Filters {@link StandardTokenizer} with {@link StandardFilter}, {@link  * LowerCaseFilter} and {@link StopFilter}.  *  * @version $Id$  */
end_comment
begin_class
DECL|class|StandardAnalyzer
specifier|public
class|class
name|StandardAnalyzer
extends|extends
name|Analyzer
block|{
DECL|field|stopSet
specifier|private
name|Set
name|stopSet
decl_stmt|;
comment|/** An array containing some common English words that are usually not   useful for searching. */
DECL|field|STOP_WORDS
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|STOP_WORDS
init|=
name|StopAnalyzer
operator|.
name|ENGLISH_STOP_WORDS
decl_stmt|;
comment|/** Builds an analyzer with the default stop words ({@link #STOP_WORDS}). */
DECL|method|StandardAnalyzer
specifier|public
name|StandardAnalyzer
parameter_list|()
block|{
name|this
argument_list|(
name|STOP_WORDS
argument_list|)
expr_stmt|;
block|}
comment|/** Builds an analyzer with the given stop words. */
DECL|method|StandardAnalyzer
specifier|public
name|StandardAnalyzer
parameter_list|(
name|String
index|[]
name|stopWords
parameter_list|)
block|{
name|stopSet
operator|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|stopWords
argument_list|)
expr_stmt|;
block|}
comment|/** Constructs a {@link StandardTokenizer} filtered by a {@link   StandardFilter}, a {@link LowerCaseFilter} and a {@link StopFilter}. */
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
name|TokenStream
name|result
init|=
operator|new
name|StandardTokenizer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|StandardFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|StopFilter
argument_list|(
name|result
argument_list|,
name|stopSet
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class
end_unit
