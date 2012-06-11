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
name|similarities
operator|.
name|Similarity
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
name|TestExplanations
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
name|TestExplanations
block|{
DECL|field|functions
specifier|private
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
comment|/** macro for payloadtermquery */
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
parameter_list|,
name|boolean
name|includeSpanScore
parameter_list|)
block|{
return|return
operator|new
name|PayloadTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
name|s
argument_list|)
argument_list|,
name|fn
argument_list|,
name|includeSpanScore
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
argument_list|,
literal|false
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
name|qtest
argument_list|(
name|pt
argument_list|(
literal|"w1"
argument_list|,
name|fn
argument_list|,
literal|true
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
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|q
operator|.
name|setBoost
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|qtest
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
name|q
operator|=
name|pt
argument_list|(
literal|"w1"
argument_list|,
name|fn
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|q
operator|.
name|setBoost
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|qtest
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
argument_list|,
literal|false
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
name|qtest
argument_list|(
name|pt
argument_list|(
literal|"xx"
argument_list|,
name|fn
argument_list|,
literal|true
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
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|q
operator|.
name|setBoost
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|qtest
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
name|q
operator|=
name|pt
argument_list|(
literal|"xx"
argument_list|,
name|fn
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|q
operator|.
name|setBoost
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|qtest
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
block|}
comment|// TODO: test the payloadnear query too!
block|}
end_class
end_unit
