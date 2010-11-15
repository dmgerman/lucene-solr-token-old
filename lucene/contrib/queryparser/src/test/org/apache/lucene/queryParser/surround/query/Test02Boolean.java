begin_unit
begin_package
DECL|package|org.apache.lucene.queryParser.surround.query
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|surround
operator|.
name|query
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestSuite
import|;
end_import
begin_import
import|import
name|junit
operator|.
name|textui
operator|.
name|TestRunner
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
name|LuceneTestCase
import|;
end_import
begin_class
DECL|class|Test02Boolean
specifier|public
class|class
name|Test02Boolean
extends|extends
name|LuceneTestCase
block|{
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|args
index|[]
parameter_list|)
block|{
name|TestRunner
operator|.
name|run
argument_list|(
operator|new
name|TestSuite
argument_list|(
name|Test02Boolean
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|field|fieldName
specifier|final
name|String
name|fieldName
init|=
literal|"bi"
decl_stmt|;
DECL|field|verbose
name|boolean
name|verbose
init|=
literal|false
decl_stmt|;
DECL|field|maxBasicQueries
name|int
name|maxBasicQueries
init|=
literal|16
decl_stmt|;
DECL|field|docs1
name|String
index|[]
name|docs1
init|=
block|{
literal|"word1 word2 word3"
block|,
literal|"word4 word5"
block|,
literal|"ord1 ord2 ord3"
block|,
literal|"orda1 orda2 orda3 word2 worda3"
block|,
literal|"a c e a b c"
block|}
decl_stmt|;
DECL|field|db1
name|SingleFieldTestDb
name|db1
init|=
operator|new
name|SingleFieldTestDb
argument_list|(
name|random
argument_list|,
name|docs1
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
DECL|method|normalTest1
specifier|public
name|void
name|normalTest1
parameter_list|(
name|String
name|query
parameter_list|,
name|int
index|[]
name|expdnrs
parameter_list|)
throws|throws
name|Exception
block|{
name|BooleanQueryTst
name|bqt
init|=
operator|new
name|BooleanQueryTst
argument_list|(
name|query
argument_list|,
name|expdnrs
argument_list|,
name|db1
argument_list|,
name|fieldName
argument_list|,
name|this
argument_list|,
operator|new
name|BasicQueryFactory
argument_list|(
name|maxBasicQueries
argument_list|)
argument_list|)
decl_stmt|;
name|bqt
operator|.
name|setVerbose
argument_list|(
name|verbose
argument_list|)
expr_stmt|;
name|bqt
operator|.
name|doTest
argument_list|()
expr_stmt|;
block|}
DECL|method|test02Terms01
specifier|public
name|void
name|test02Terms01
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|}
decl_stmt|;
name|normalTest1
argument_list|(
literal|"word1"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test02Terms02
specifier|public
name|void
name|test02Terms02
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|,
literal|1
block|,
literal|3
block|}
decl_stmt|;
name|normalTest1
argument_list|(
literal|"word*"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test02Terms03
specifier|public
name|void
name|test02Terms03
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|2
block|}
decl_stmt|;
name|normalTest1
argument_list|(
literal|"ord2"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test02Terms04
specifier|public
name|void
name|test02Terms04
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{}
decl_stmt|;
name|normalTest1
argument_list|(
literal|"kxork*"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test02Terms05
specifier|public
name|void
name|test02Terms05
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|,
literal|1
block|,
literal|3
block|}
decl_stmt|;
name|normalTest1
argument_list|(
literal|"wor*"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test02Terms06
specifier|public
name|void
name|test02Terms06
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{}
decl_stmt|;
name|normalTest1
argument_list|(
literal|"ab"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test02Terms10
specifier|public
name|void
name|test02Terms10
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{}
decl_stmt|;
name|normalTest1
argument_list|(
literal|"abc?"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test02Terms13
specifier|public
name|void
name|test02Terms13
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|,
literal|1
block|,
literal|3
block|}
decl_stmt|;
name|normalTest1
argument_list|(
literal|"word?"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test02Terms14
specifier|public
name|void
name|test02Terms14
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|,
literal|1
block|,
literal|3
block|}
decl_stmt|;
name|normalTest1
argument_list|(
literal|"w?rd?"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test02Terms20
specifier|public
name|void
name|test02Terms20
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|,
literal|1
block|,
literal|3
block|}
decl_stmt|;
name|normalTest1
argument_list|(
literal|"w*rd?"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test02Terms21
specifier|public
name|void
name|test02Terms21
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|3
block|}
decl_stmt|;
name|normalTest1
argument_list|(
literal|"w*rd??"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test02Terms22
specifier|public
name|void
name|test02Terms22
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|3
block|}
decl_stmt|;
name|normalTest1
argument_list|(
literal|"w*?da?"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test02Terms23
specifier|public
name|void
name|test02Terms23
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{}
decl_stmt|;
name|normalTest1
argument_list|(
literal|"w?da?"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test03And01
specifier|public
name|void
name|test03And01
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|}
decl_stmt|;
name|normalTest1
argument_list|(
literal|"word1 AND word2"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test03And02
specifier|public
name|void
name|test03And02
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|3
block|}
decl_stmt|;
name|normalTest1
argument_list|(
literal|"word* and ord*"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test03And03
specifier|public
name|void
name|test03And03
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|}
decl_stmt|;
name|normalTest1
argument_list|(
literal|"and(word1,word2)"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test04Or01
specifier|public
name|void
name|test04Or01
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|,
literal|3
block|}
decl_stmt|;
name|normalTest1
argument_list|(
literal|"word1 or word2"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test04Or02
specifier|public
name|void
name|test04Or02
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
decl_stmt|;
name|normalTest1
argument_list|(
literal|"word* OR ord*"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test04Or03
specifier|public
name|void
name|test04Or03
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|,
literal|3
block|}
decl_stmt|;
name|normalTest1
argument_list|(
literal|"OR (word1, word2)"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test05Not01
specifier|public
name|void
name|test05Not01
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|3
block|}
decl_stmt|;
name|normalTest1
argument_list|(
literal|"word2 NOT word1"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test05Not02
specifier|public
name|void
name|test05Not02
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|}
decl_stmt|;
name|normalTest1
argument_list|(
literal|"word2* not ord*"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test06AndOr01
specifier|public
name|void
name|test06AndOr01
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|}
decl_stmt|;
name|normalTest1
argument_list|(
literal|"(word1 or ab)and or(word2,xyz, defg)"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test07AndOrNot02
specifier|public
name|void
name|test07AndOrNot02
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|}
decl_stmt|;
name|normalTest1
argument_list|(
literal|"or( word2* not ord*, and(xyz,def))"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
