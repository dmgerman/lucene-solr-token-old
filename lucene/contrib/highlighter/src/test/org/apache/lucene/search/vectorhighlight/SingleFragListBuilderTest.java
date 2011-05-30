begin_unit
begin_package
DECL|package|org.apache.lucene.search.vectorhighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|vectorhighlight
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
name|search
operator|.
name|Query
import|;
end_import
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|SingleFragListBuilderTest
specifier|public
class|class
name|SingleFragListBuilderTest
extends|extends
name|AbstractTestCase
block|{
DECL|method|testNullFieldFragList
specifier|public
name|void
name|testNullFieldFragList
parameter_list|()
throws|throws
name|Exception
block|{
name|SingleFragListBuilder
name|sflb
init|=
operator|new
name|SingleFragListBuilder
argument_list|()
decl_stmt|;
name|FieldFragList
name|ffl
init|=
name|sflb
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|(
literal|"a"
argument_list|,
literal|"b c d"
argument_list|)
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ffl
operator|.
name|getFragInfos
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testShortFieldFragList
specifier|public
name|void
name|testShortFieldFragList
parameter_list|()
throws|throws
name|Exception
block|{
name|SingleFragListBuilder
name|sflb
init|=
operator|new
name|SingleFragListBuilder
argument_list|()
decl_stmt|;
name|FieldFragList
name|ffl
init|=
name|sflb
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|(
literal|"a"
argument_list|,
literal|"a b c d"
argument_list|)
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ffl
operator|.
name|getFragInfos
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"subInfos=(a((0,1)))/1.0(0,2147483647)"
argument_list|,
name|ffl
operator|.
name|getFragInfos
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testLongFieldFragList
specifier|public
name|void
name|testLongFieldFragList
parameter_list|()
throws|throws
name|Exception
block|{
name|SingleFragListBuilder
name|sflb
init|=
operator|new
name|SingleFragListBuilder
argument_list|()
decl_stmt|;
name|FieldFragList
name|ffl
init|=
name|sflb
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|(
literal|"a"
argument_list|,
literal|"a b c d"
argument_list|,
literal|"a b c d e f g h i"
argument_list|,
literal|"j k l m n o p q r s t u v w x y z a b c"
argument_list|,
literal|"d e f g"
argument_list|)
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ffl
operator|.
name|getFragInfos
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"subInfos=(a((0,1))a((8,9))a((60,61)))/3.0(0,2147483647)"
argument_list|,
name|ffl
operator|.
name|getFragInfos
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|fpl
specifier|private
name|FieldPhraseList
name|fpl
parameter_list|(
name|String
name|queryValue
parameter_list|,
name|String
modifier|...
name|indexValues
parameter_list|)
throws|throws
name|Exception
block|{
name|make1dmfIndex
argument_list|(
name|indexValues
argument_list|)
expr_stmt|;
name|Query
name|query
init|=
name|paW
operator|.
name|parse
argument_list|(
name|queryValue
argument_list|)
decl_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|query
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
return|return
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
return|;
block|}
block|}
end_class
end_unit
