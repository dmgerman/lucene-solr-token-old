begin_unit
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package
begin_comment
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import
begin_comment
comment|/**  * @author yonik  */
end_comment
begin_class
DECL|class|TestStopFilter
specifier|public
class|class
name|TestStopFilter
extends|extends
name|TestCase
block|{
comment|// other StopFilter functionality is already tested by TestStopAnalyzer
DECL|method|testExactCase
specifier|public
name|void
name|testExactCase
parameter_list|()
throws|throws
name|IOException
block|{
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"Now is The Time"
argument_list|)
decl_stmt|;
name|String
index|[]
name|stopWords
init|=
operator|new
name|String
index|[]
block|{
literal|"is"
block|,
literal|"the"
block|,
literal|"Time"
block|}
decl_stmt|;
name|TokenStream
name|stream
init|=
operator|new
name|StopFilter
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|reader
argument_list|)
argument_list|,
name|stopWords
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Now"
argument_list|,
name|stream
operator|.
name|next
argument_list|()
operator|.
name|termText
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The"
argument_list|,
name|stream
operator|.
name|next
argument_list|()
operator|.
name|termText
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|stream
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testIgnoreCase
specifier|public
name|void
name|testIgnoreCase
parameter_list|()
throws|throws
name|IOException
block|{
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"Now is The Time"
argument_list|)
decl_stmt|;
name|String
index|[]
name|stopWords
init|=
operator|new
name|String
index|[]
block|{
literal|"is"
block|,
literal|"the"
block|,
literal|"Time"
block|}
decl_stmt|;
name|TokenStream
name|stream
init|=
operator|new
name|StopFilter
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|reader
argument_list|)
argument_list|,
name|stopWords
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Now"
argument_list|,
name|stream
operator|.
name|next
argument_list|()
operator|.
name|termText
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|stream
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
