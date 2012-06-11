begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.tasks
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|tasks
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
name|benchmark
operator|.
name|byTask
operator|.
name|PerfRunData
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
name|Analyzer
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
name|highlight
operator|.
name|SimpleHTMLFormatter
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
name|highlight
operator|.
name|Highlighter
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
name|highlight
operator|.
name|TextFragment
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
name|highlight
operator|.
name|QueryScorer
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
name|highlight
operator|.
name|TokenSources
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
name|Query
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
name|Document
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
name|IndexReader
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
comment|/**  * Test Search task which counts number of searches.  */
end_comment
begin_class
DECL|class|CountingHighlighterTestTask
specifier|public
class|class
name|CountingHighlighterTestTask
extends|extends
name|SearchTravRetHighlightTask
block|{
DECL|field|numHighlightedResults
specifier|public
specifier|static
name|int
name|numHighlightedResults
init|=
literal|0
decl_stmt|;
DECL|field|numDocsRetrieved
specifier|public
specifier|static
name|int
name|numDocsRetrieved
init|=
literal|0
decl_stmt|;
DECL|method|CountingHighlighterTestTask
specifier|public
name|CountingHighlighterTestTask
parameter_list|(
name|PerfRunData
name|runData
parameter_list|)
block|{
name|super
argument_list|(
name|runData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|retrieveDoc
specifier|protected
name|Document
name|retrieveDoc
parameter_list|(
name|IndexReader
name|ir
parameter_list|,
name|int
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|Document
name|document
init|=
name|ir
operator|.
name|document
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|document
operator|!=
literal|null
condition|)
block|{
name|numDocsRetrieved
operator|++
expr_stmt|;
block|}
return|return
name|document
return|;
block|}
annotation|@
name|Override
DECL|method|getBenchmarkHighlighter
specifier|public
name|BenchmarkHighlighter
name|getBenchmarkHighlighter
parameter_list|(
name|Query
name|q
parameter_list|)
block|{
name|highlighter
operator|=
operator|new
name|Highlighter
argument_list|(
operator|new
name|SimpleHTMLFormatter
argument_list|()
argument_list|,
operator|new
name|QueryScorer
argument_list|(
name|q
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|BenchmarkHighlighter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|doHighlight
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|doc
parameter_list|,
name|String
name|field
parameter_list|,
name|Document
name|document
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|String
name|text
parameter_list|)
throws|throws
name|Exception
block|{
name|TokenStream
name|ts
init|=
name|TokenSources
operator|.
name|getAnyTokenStream
argument_list|(
name|reader
argument_list|,
name|doc
argument_list|,
name|field
argument_list|,
name|document
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|TextFragment
index|[]
name|frag
init|=
name|highlighter
operator|.
name|getBestTextFragments
argument_list|(
name|ts
argument_list|,
name|text
argument_list|,
name|mergeContiguous
argument_list|,
name|maxFrags
argument_list|)
decl_stmt|;
name|numHighlightedResults
operator|+=
name|frag
operator|!=
literal|null
condition|?
name|frag
operator|.
name|length
else|:
literal|0
expr_stmt|;
return|return
name|frag
operator|!=
literal|null
condition|?
name|frag
operator|.
name|length
else|:
literal|0
return|;
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
