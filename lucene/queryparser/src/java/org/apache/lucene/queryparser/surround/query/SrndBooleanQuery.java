begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.queryparser.surround.query
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|surround
operator|.
name|query
package|;
end_package
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
begin_class
DECL|class|SrndBooleanQuery
class|class
name|SrndBooleanQuery
block|{
DECL|method|addQueriesToBoolean
specifier|public
specifier|static
name|void
name|addQueriesToBoolean
parameter_list|(
name|BooleanQuery
operator|.
name|Builder
name|bq
parameter_list|,
name|List
argument_list|<
name|Query
argument_list|>
name|queries
parameter_list|,
name|BooleanClause
operator|.
name|Occur
name|occur
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|queries
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|bq
operator|.
name|add
argument_list|(
name|queries
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|occur
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|makeBooleanQuery
specifier|public
specifier|static
name|Query
name|makeBooleanQuery
parameter_list|(
name|List
argument_list|<
name|Query
argument_list|>
name|queries
parameter_list|,
name|BooleanClause
operator|.
name|Occur
name|occur
parameter_list|)
block|{
if|if
condition|(
name|queries
operator|.
name|size
argument_list|()
operator|<=
literal|1
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Too few subqueries: "
operator|+
name|queries
operator|.
name|size
argument_list|()
argument_list|)
throw|;
block|}
name|BooleanQuery
operator|.
name|Builder
name|bq
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|addQueriesToBoolean
argument_list|(
name|bq
argument_list|,
name|queries
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|queries
operator|.
name|size
argument_list|()
argument_list|)
argument_list|,
name|occur
argument_list|)
expr_stmt|;
return|return
name|bq
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class
end_unit
