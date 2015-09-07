begin_unit
begin_package
DECL|package|org.apache.lucene.search.payloads
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|payloads
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
name|BaseExplanationTestCase
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
name|similarities
operator|.
name|DefaultSimilarity
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
name|spans
operator|.
name|SpanBoostQuery
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
name|spans
operator|.
name|SpanNearQuery
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
name|spans
operator|.
name|SpanOrQuery
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
name|spans
operator|.
name|SpanQuery
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
name|spans
operator|.
name|SpanTermQuery
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
name|BytesRef
import|;
end_import
begin_comment
comment|/**  * TestExplanations subclass focusing on payload queries  */
end_comment
begin_class
DECL|class|TestPayloadExplanations
specifier|public
class|class
name|TestPayloadExplanations
extends|extends
name|BaseExplanationTestCase
block|{
DECL|field|functions
specifier|private
specifier|static
name|PayloadFunction
name|functions
index|[]
init|=
operator|new
name|PayloadFunction
index|[]
block|{
operator|new
name|AveragePayloadFunction
argument_list|()
block|,
operator|new
name|MinPayloadFunction
argument_list|()
block|,
operator|new
name|MaxPayloadFunction
argument_list|()
block|,   }
decl_stmt|;
annotation|@
name|Override
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
name|searcher
operator|.
name|setSimilarity
argument_list|(
operator|new
name|DefaultSimilarity
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|float
name|scorePayload
parameter_list|(
name|int
name|doc
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|BytesRef
name|payload
parameter_list|)
block|{
return|return
literal|1
operator|+
operator|(
name|payload
operator|.
name|hashCode
argument_list|()
operator|%
literal|10
operator|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** macro for payloadscorequery */
DECL|method|pt
specifier|private
name|SpanQuery
name|pt
parameter_list|(
name|String
name|s
parameter_list|,
name|PayloadFunction
name|fn
parameter_list|)
block|{
return|return
operator|new
name|PayloadScoreQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
name|s
argument_list|)
argument_list|)
argument_list|,
name|fn
argument_list|)
return|;
block|}
comment|/* simple PayloadTermQueries */
DECL|method|testPT1
specifier|public
name|void
name|testPT1
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|PayloadFunction
name|fn
range|:
name|functions
control|)
block|{
name|qtest
argument_list|(
name|pt
argument_list|(
literal|"w1"
argument_list|,
name|fn
argument_list|)
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testPT2
specifier|public
name|void
name|testPT2
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|PayloadFunction
name|fn
range|:
name|functions
control|)
block|{
name|SpanQuery
name|q
init|=
name|pt
argument_list|(
literal|"w1"
argument_list|,
name|fn
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
operator|new
name|SpanBoostQuery
argument_list|(
name|q
argument_list|,
literal|1000
argument_list|)
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testPT4
specifier|public
name|void
name|testPT4
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|PayloadFunction
name|fn
range|:
name|functions
control|)
block|{
name|qtest
argument_list|(
name|pt
argument_list|(
literal|"xx"
argument_list|,
name|fn
argument_list|)
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testPT5
specifier|public
name|void
name|testPT5
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|PayloadFunction
name|fn
range|:
name|functions
control|)
block|{
name|SpanQuery
name|q
init|=
name|pt
argument_list|(
literal|"xx"
argument_list|,
name|fn
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
operator|new
name|SpanBoostQuery
argument_list|(
name|q
argument_list|,
literal|1000
argument_list|)
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
block|}
comment|// TODO: test the payloadnear query too!
comment|/*     protected static final String[] docFields = {     "w1 w2 w3 w4 w5",     "w1 w3 w2 w3 zz",     "w1 xx w2 yy w3",     "w1 w3 xx w2 yy w3 zz"   };    */
DECL|method|testAllFunctions
specifier|public
name|void
name|testAllFunctions
parameter_list|(
name|SpanQuery
name|query
parameter_list|,
name|int
index|[]
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|PayloadFunction
name|fn
range|:
name|functions
control|)
block|{
name|qtest
argument_list|(
operator|new
name|PayloadScoreQuery
argument_list|(
name|query
argument_list|,
name|fn
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSimpleTerm
specifier|public
name|void
name|testSimpleTerm
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanTermQuery
name|q
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w2"
argument_list|)
argument_list|)
decl_stmt|;
name|testAllFunctions
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testOrTerm
specifier|public
name|void
name|testOrTerm
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanOrQuery
name|q
init|=
operator|new
name|SpanOrQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"xx"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"yy"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|testAllFunctions
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testOrderedNearQuery
specifier|public
name|void
name|testOrderedNearQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanNearQuery
name|q
init|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w3"
argument_list|)
argument_list|)
block|,
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w2"
argument_list|)
argument_list|)
block|}
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|testAllFunctions
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnorderedNearQuery
specifier|public
name|void
name|testUnorderedNearQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanNearQuery
name|q
init|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w2"
argument_list|)
argument_list|)
block|,
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w3"
argument_list|)
argument_list|)
block|}
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|testAllFunctions
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
