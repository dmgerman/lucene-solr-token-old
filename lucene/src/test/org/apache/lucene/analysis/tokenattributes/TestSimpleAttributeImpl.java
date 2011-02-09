begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.tokenattributes
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tokenattributes
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|_TestUtil
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_class
DECL|class|TestSimpleAttributeImpl
specifier|public
class|class
name|TestSimpleAttributeImpl
extends|extends
name|LuceneTestCase
block|{
comment|// this checks using reflection API if the defaults are correct
DECL|method|testAttributes
specifier|public
name|void
name|testAttributes
parameter_list|()
block|{
name|_TestUtil
operator|.
name|assertAttributeReflection
argument_list|(
operator|new
name|PositionIncrementAttributeImpl
argument_list|()
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#positionIncrement"
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|_TestUtil
operator|.
name|assertAttributeReflection
argument_list|(
operator|new
name|FlagsAttributeImpl
argument_list|()
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|FlagsAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#flags"
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|_TestUtil
operator|.
name|assertAttributeReflection
argument_list|(
operator|new
name|TypeAttributeImpl
argument_list|()
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|TypeAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#type"
argument_list|,
name|TypeAttribute
operator|.
name|DEFAULT_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|_TestUtil
operator|.
name|assertAttributeReflection
argument_list|(
operator|new
name|PayloadAttributeImpl
argument_list|()
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|PayloadAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#payload"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|_TestUtil
operator|.
name|assertAttributeReflection
argument_list|(
operator|new
name|KeywordAttributeImpl
argument_list|()
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|KeywordAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#keyword"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|_TestUtil
operator|.
name|assertAttributeReflection
argument_list|(
operator|new
name|OffsetAttributeImpl
argument_list|()
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
block|{
block|{
name|put
argument_list|(
name|OffsetAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#startOffset"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|OffsetAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#endOffset"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
