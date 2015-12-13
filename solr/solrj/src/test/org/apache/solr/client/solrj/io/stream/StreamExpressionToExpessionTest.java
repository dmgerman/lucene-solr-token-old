begin_unit
begin_package
DECL|package|org.apache.solr.client.solrj.io.stream
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
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
name|util
operator|.
name|LuceneTestCase
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|ops
operator|.
name|GroupOperation
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpressionParser
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamFactory
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|metrics
operator|.
name|CountMetric
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|metrics
operator|.
name|MaxMetric
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|metrics
operator|.
name|MeanMetric
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|metrics
operator|.
name|Metric
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|metrics
operator|.
name|MinMetric
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|metrics
operator|.
name|SumMetric
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_comment
comment|/**  **/
end_comment
begin_class
DECL|class|StreamExpressionToExpessionTest
specifier|public
class|class
name|StreamExpressionToExpessionTest
extends|extends
name|LuceneTestCase
block|{
DECL|field|factory
specifier|private
name|StreamFactory
name|factory
decl_stmt|;
DECL|method|StreamExpressionToExpessionTest
specifier|public
name|StreamExpressionToExpessionTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|factory
operator|=
operator|new
name|StreamFactory
argument_list|()
operator|.
name|withCollectionZkHost
argument_list|(
literal|"collection1"
argument_list|,
literal|"testhost:1234"
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"search"
argument_list|,
name|CloudSolrStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"merge"
argument_list|,
name|MergeStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"unique"
argument_list|,
name|UniqueStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"top"
argument_list|,
name|RankStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"reduce"
argument_list|,
name|ReducerStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"group"
argument_list|,
name|GroupOperation
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"stats"
argument_list|,
name|StatsStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"facet"
argument_list|,
name|FacetStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"count"
argument_list|,
name|CountMetric
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"sum"
argument_list|,
name|SumMetric
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"min"
argument_list|,
name|MinMetric
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"max"
argument_list|,
name|MaxMetric
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"avg"
argument_list|,
name|MeanMetric
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCloudSolrStream
specifier|public
name|void
name|testCloudSolrStream
parameter_list|()
throws|throws
name|Exception
block|{
name|CloudSolrStream
name|stream
decl_stmt|;
name|String
name|expressionString
decl_stmt|;
comment|// Basic test
name|stream
operator|=
operator|new
name|CloudSolrStream
argument_list|(
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
literal|"search(collection1, q=*:*, fl=\"id,a_s,a_i,a_f\", sort=\"a_f asc, a_i asc\")"
argument_list|)
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|expressionString
operator|=
name|stream
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"search(collection1,"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"q=\"*:*\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"fl=\"id,a_s,a_i,a_f\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"sort=\"a_f asc, a_i asc\""
argument_list|)
argument_list|)
expr_stmt|;
comment|// Basic w/aliases
name|stream
operator|=
operator|new
name|CloudSolrStream
argument_list|(
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
literal|"search(collection1, q=*:*, fl=\"id,a_s,a_i,a_f\", sort=\"a_f asc, a_i asc\", aliases=\"id=izzy,a_s=kayden\")"
argument_list|)
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|expressionString
operator|=
name|stream
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"id=izzy"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"a_s=kayden"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStatsStream
specifier|public
name|void
name|testStatsStream
parameter_list|()
throws|throws
name|Exception
block|{
name|StatsStream
name|stream
decl_stmt|;
name|String
name|expressionString
decl_stmt|;
comment|// Basic test
name|stream
operator|=
operator|new
name|StatsStream
argument_list|(
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
literal|"stats(collection1, q=*:*, fl=\"id,a_s,a_i,a_f\", sort=\"a_f asc, a_i asc\", sum(a_i), avg(a_i), count(*), min(a_i), max(a_i))"
argument_list|)
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|expressionString
operator|=
name|stream
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"stats(collection1,"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"q=\"*:*\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"fl=\"id,a_s,a_i,a_f\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"sort=\"a_f asc, a_i asc\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"min(a_i)"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"max(a_i)"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"avg(a_i)"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"count(*)"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"sum(a_i)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUniqueStream
specifier|public
name|void
name|testUniqueStream
parameter_list|()
throws|throws
name|Exception
block|{
name|UniqueStream
name|stream
decl_stmt|;
name|String
name|expressionString
decl_stmt|;
comment|// Basic test
name|stream
operator|=
operator|new
name|UniqueStream
argument_list|(
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
literal|"unique(search(collection1, q=*:*, fl=\"id,a_s,a_i,a_f\", sort=\"a_f asc, a_i asc\"), over=\"a_f\")"
argument_list|)
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|expressionString
operator|=
name|stream
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"unique(search(collection1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"over=a_f"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMergeStream
specifier|public
name|void
name|testMergeStream
parameter_list|()
throws|throws
name|Exception
block|{
name|MergeStream
name|stream
decl_stmt|;
name|String
name|expressionString
decl_stmt|;
comment|// Basic test
name|stream
operator|=
operator|new
name|MergeStream
argument_list|(
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
literal|"merge("
operator|+
literal|"search(collection1, q=\"id:(0 3 4)\", fl=\"id,a_s,a_i,a_f\", sort=\"a_f asc, a_s asc\"),"
operator|+
literal|"search(collection1, q=\"id:(1 2)\", fl=\"id,a_s,a_i,a_f\", sort=\"a_f asc, a_s asc\"),"
operator|+
literal|"on=\"a_f asc, a_s asc\")"
argument_list|)
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|expressionString
operator|=
name|stream
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"q=\"id:(0 3 4)\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"q=\"id:(1 2)\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"on=\"a_f asc,a_s asc\""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRankStream
specifier|public
name|void
name|testRankStream
parameter_list|()
throws|throws
name|Exception
block|{
name|RankStream
name|stream
decl_stmt|;
name|String
name|expressionString
decl_stmt|;
comment|// Basic test
name|stream
operator|=
operator|new
name|RankStream
argument_list|(
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
literal|"top("
operator|+
literal|"n=3,"
operator|+
literal|"search(collection1, q=*:*, fl=\"id,a_s,a_i,a_f\", sort=\"a_f asc,a_i asc\"),"
operator|+
literal|"sort=\"a_f asc, a_i asc\")"
argument_list|)
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|expressionString
operator|=
name|stream
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"top(n=3,search(collection1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"sort=\"a_f asc,a_i asc\""
argument_list|)
argument_list|)
expr_stmt|;
comment|// find 2nd instance of sort
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|substring
argument_list|(
name|expressionString
operator|.
name|indexOf
argument_list|(
literal|"sort=\"a_f asc,a_i asc\""
argument_list|)
operator|+
literal|1
argument_list|)
operator|.
name|contains
argument_list|(
literal|"sort=\"a_f asc,a_i asc\""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReducerStream
specifier|public
name|void
name|testReducerStream
parameter_list|()
throws|throws
name|Exception
block|{
name|ReducerStream
name|stream
decl_stmt|;
name|String
name|expressionString
decl_stmt|;
comment|// Basic test
name|stream
operator|=
operator|new
name|ReducerStream
argument_list|(
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
literal|"reduce("
operator|+
literal|"search(collection1, q=*:*, fl=\"id,a_s,a_i,a_f\", sort=\"a_s desc, a_f asc\"),"
operator|+
literal|"by=\"a_s\", group(sort=\"a_i desc\", n=\"5\"))"
argument_list|)
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|expressionString
operator|=
name|stream
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"reduce(search(collection1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"by=a_s"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFacetStream
specifier|public
name|void
name|testFacetStream
parameter_list|()
throws|throws
name|Exception
block|{
name|FacetStream
name|stream
decl_stmt|;
name|String
name|expressionString
decl_stmt|;
comment|// Basic test
name|stream
operator|=
operator|new
name|FacetStream
argument_list|(
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
literal|"facet("
operator|+
literal|"collection1, "
operator|+
literal|"q=\"*:*\", "
operator|+
literal|"buckets=\"a_s\", "
operator|+
literal|"bucketSorts=\"sum(a_i) asc\", "
operator|+
literal|"bucketSizeLimit=100, "
operator|+
literal|"sum(a_i), sum(a_f), "
operator|+
literal|"min(a_i), min(a_f), "
operator|+
literal|"max(a_i), max(a_f), "
operator|+
literal|"avg(a_i), avg(a_f), "
operator|+
literal|"count(*)"
operator|+
literal|")"
argument_list|)
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|expressionString
operator|=
name|stream
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"facet(collection1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"q=\"*:*\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"buckets=a_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"bucketSorts=\"sum(a_i) asc\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"bucketSizeLimit=100"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"sum(a_i)"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"sum(a_f)"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"min(a_i)"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"min(a_f)"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"max(a_i)"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"max(a_f)"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"avg(a_i)"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"avg(a_f)"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expressionString
operator|.
name|contains
argument_list|(
literal|"count(*)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCountMetric
specifier|public
name|void
name|testCountMetric
parameter_list|()
throws|throws
name|Exception
block|{
name|Metric
name|metric
decl_stmt|;
name|String
name|expressionString
decl_stmt|;
comment|// Basic test
name|metric
operator|=
operator|new
name|CountMetric
argument_list|(
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
literal|"count(*)"
argument_list|)
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|expressionString
operator|=
name|metric
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"count(*)"
argument_list|,
name|expressionString
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMaxMetric
specifier|public
name|void
name|testMaxMetric
parameter_list|()
throws|throws
name|Exception
block|{
name|Metric
name|metric
decl_stmt|;
name|String
name|expressionString
decl_stmt|;
comment|// Basic test
name|metric
operator|=
operator|new
name|MaxMetric
argument_list|(
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
literal|"max(foo)"
argument_list|)
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|expressionString
operator|=
name|metric
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"max(foo)"
argument_list|,
name|expressionString
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMinMetric
specifier|public
name|void
name|testMinMetric
parameter_list|()
throws|throws
name|Exception
block|{
name|Metric
name|metric
decl_stmt|;
name|String
name|expressionString
decl_stmt|;
comment|// Basic test
name|metric
operator|=
operator|new
name|MinMetric
argument_list|(
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
literal|"min(foo)"
argument_list|)
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|expressionString
operator|=
name|metric
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"min(foo)"
argument_list|,
name|expressionString
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMeanMetric
specifier|public
name|void
name|testMeanMetric
parameter_list|()
throws|throws
name|Exception
block|{
name|Metric
name|metric
decl_stmt|;
name|String
name|expressionString
decl_stmt|;
comment|// Basic test
name|metric
operator|=
operator|new
name|MeanMetric
argument_list|(
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
literal|"avg(foo)"
argument_list|)
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|expressionString
operator|=
name|metric
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"avg(foo)"
argument_list|,
name|expressionString
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSumMetric
specifier|public
name|void
name|testSumMetric
parameter_list|()
throws|throws
name|Exception
block|{
name|Metric
name|metric
decl_stmt|;
name|String
name|expressionString
decl_stmt|;
comment|// Basic test
name|metric
operator|=
operator|new
name|SumMetric
argument_list|(
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
literal|"sum(foo)"
argument_list|)
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|expressionString
operator|=
name|metric
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"sum(foo)"
argument_list|,
name|expressionString
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
