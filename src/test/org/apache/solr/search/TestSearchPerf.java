begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|index
operator|.
name|Term
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
name|TermQuery
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
name|search
operator|.
name|BooleanQuery
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
name|BooleanClause
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|AbstractSolrTestCase
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|SolrQueryRequest
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|SolrQueryResponse
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
operator|.
name|UpdateRequestProcessorChain
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
operator|.
name|UpdateRequestProcessor
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|AddUpdateCommand
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|UpdateParams
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrInputDocument
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
DECL|class|TestSearchPerf
specifier|public
class|class
name|TestSearchPerf
extends|extends
name|AbstractSolrTestCase
block|{
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema11.xml"
return|;
block|}
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig.xml"
return|;
block|}
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|t
name|String
name|t
parameter_list|(
name|int
name|tnum
parameter_list|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%08d"
argument_list|,
name|tnum
argument_list|)
return|;
block|}
DECL|field|r
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|nDocs
name|int
name|nDocs
decl_stmt|;
DECL|method|createIndex
name|void
name|createIndex
parameter_list|(
name|int
name|nDocs
parameter_list|)
block|{
name|this
operator|.
name|nDocs
operator|=
name|nDocs
expr_stmt|;
name|assertU
argument_list|(
name|delQ
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nDocs
condition|;
name|i
operator|++
control|)
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Float
operator|.
name|toString
argument_list|(
name|i
argument_list|)
comment|//             ,"foo1_s",t(0)
comment|//             ,"foo2_s",t(r.nextInt(2))
comment|//             ,"foo4_s",t(r.nextInt(3))
argument_list|,
literal|"foomany_s"
argument_list|,
name|t
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
name|nDocs
operator|*
literal|10
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// assertU(optimize()); // squeeze out any possible deleted docs
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|createIndex2
name|void
name|createIndex2
parameter_list|(
name|int
name|nDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrQueryRequest
name|req
init|=
name|lrf
operator|.
name|makeRequest
argument_list|()
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|UpdateRequestProcessorChain
name|processorChain
init|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getUpdateProcessingChain
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|UpdateRequestProcessor
name|processor
init|=
name|processorChain
operator|.
name|createProcessor
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nDocs
condition|;
name|i
operator|++
control|)
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
name|Float
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"foomany_s"
argument_list|,
name|t
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
name|nDocs
operator|*
literal|10
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|AddUpdateCommand
name|cmd
init|=
operator|new
name|AddUpdateCommand
argument_list|()
decl_stmt|;
name|cmd
operator|.
name|solrDoc
operator|=
name|doc
expr_stmt|;
name|processor
operator|.
name|processAdd
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
name|processor
operator|.
name|finish
argument_list|()
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|req
operator|=
name|lrf
operator|.
name|makeRequest
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|nDocs
argument_list|,
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|doSetGen
name|int
name|doSetGen
parameter_list|(
name|int
name|iter
parameter_list|,
name|Query
name|q
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrQueryRequest
name|req
init|=
name|lrf
operator|.
name|makeRequest
argument_list|()
decl_stmt|;
name|SolrIndexSearcher
name|searcher
init|=
name|req
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|int
name|ret
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iter
condition|;
name|i
operator|++
control|)
block|{
name|DocSet
name|set
init|=
name|searcher
operator|.
name|getDocSetNC
argument_list|(
name|q
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|ret
operator|+=
name|set
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ret="
operator|+
name|ret
operator|+
literal|" time="
operator|+
operator|(
name|end
operator|-
name|start
operator|)
operator|+
literal|" throughput="
operator|+
name|iter
operator|*
literal|1000
operator|/
operator|(
name|end
operator|-
name|start
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|ret
return|;
block|}
comment|// prevent complaints by junit
DECL|method|testEmpty
specifier|public
name|void
name|testEmpty
parameter_list|()
block|{   }
DECL|method|XtestSetGenerationPerformance
specifier|public
name|void
name|XtestSetGenerationPerformance
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndex
argument_list|(
literal|49999
argument_list|)
expr_stmt|;
name|doSetGen
argument_list|(
literal|10000
argument_list|,
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo1_s"
argument_list|,
name|t
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo2_s"
argument_list|,
name|t
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo2_s"
argument_list|,
name|t
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|doSetGen
argument_list|(
literal|5000
argument_list|,
name|bq
argument_list|)
expr_stmt|;
block|}
comment|/** test range query performance */
DECL|method|XtestRangePerformance
specifier|public
name|void
name|XtestRangePerformance
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|indexSize
init|=
literal|199999
decl_stmt|;
name|float
name|fractionCovered
init|=
literal|1.0f
decl_stmt|;
name|String
name|l
init|=
name|t
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|u
init|=
name|t
argument_list|(
call|(
name|int
call|)
argument_list|(
name|indexSize
operator|*
literal|10
operator|*
name|fractionCovered
argument_list|)
argument_list|)
decl_stmt|;
name|SolrQueryRequest
name|req
init|=
name|lrf
operator|.
name|makeRequest
argument_list|()
decl_stmt|;
name|QParser
name|parser
init|=
name|QParser
operator|.
name|getParser
argument_list|(
literal|"foomany_s:["
operator|+
name|l
operator|+
literal|" TO "
operator|+
name|u
operator|+
literal|"]"
argument_list|,
literal|null
argument_list|,
name|req
argument_list|)
decl_stmt|;
name|Query
name|range
init|=
name|parser
operator|.
name|parse
argument_list|()
decl_stmt|;
name|QParser
name|parser2
init|=
name|QParser
operator|.
name|getParser
argument_list|(
literal|"{!frange l="
operator|+
name|l
operator|+
literal|" u="
operator|+
name|u
operator|+
literal|"}foomany_s"
argument_list|,
literal|null
argument_list|,
name|req
argument_list|)
decl_stmt|;
name|Query
name|frange
init|=
name|parser2
operator|.
name|parse
argument_list|()
decl_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
name|createIndex2
argument_list|(
name|indexSize
argument_list|)
expr_stmt|;
name|doSetGen
argument_list|(
literal|1
argument_list|,
name|range
argument_list|)
expr_stmt|;
name|doSetGen
argument_list|(
literal|1
argument_list|,
name|frange
argument_list|)
expr_stmt|;
comment|// load field cache
name|doSetGen
argument_list|(
literal|100
argument_list|,
name|range
argument_list|)
expr_stmt|;
name|doSetGen
argument_list|(
literal|10000
argument_list|,
name|frange
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
