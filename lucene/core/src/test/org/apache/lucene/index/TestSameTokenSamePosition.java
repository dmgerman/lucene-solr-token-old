begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|tokenattributes
operator|.
name|OffsetAttribute
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
name|tokenattributes
operator|.
name|PositionIncrementAttribute
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
name|document
operator|.
name|TextField
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
name|store
operator|.
name|Directory
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
DECL|class|TestSameTokenSamePosition
specifier|public
class|class
name|TestSameTokenSamePosition
extends|extends
name|LuceneTestCase
block|{
comment|/**    * Attempt to reproduce an assertion error that happens    * only with the trunk version around April 2011.    */
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|riw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"eng"
argument_list|,
operator|new
name|BugReproTokenStream
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|riw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|riw
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Same as the above, but with more docs    */
DECL|method|testMoreDocs
specifier|public
name|void
name|testMoreDocs
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|riw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"eng"
argument_list|,
operator|new
name|BugReproTokenStream
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|riw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|riw
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
begin_class
DECL|class|BugReproTokenStream
specifier|final
class|class
name|BugReproTokenStream
extends|extends
name|TokenStream
block|{
DECL|field|termAtt
specifier|private
specifier|final
name|CharTermAttribute
name|termAtt
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|offsetAtt
specifier|private
specifier|final
name|OffsetAttribute
name|offsetAtt
init|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|posIncAtt
specifier|private
specifier|final
name|PositionIncrementAttribute
name|posIncAtt
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|tokenCount
specifier|private
specifier|final
name|int
name|tokenCount
init|=
literal|4
decl_stmt|;
DECL|field|nextTokenIndex
specifier|private
name|int
name|nextTokenIndex
init|=
literal|0
decl_stmt|;
DECL|field|terms
specifier|private
specifier|final
name|String
name|terms
index|[]
init|=
operator|new
name|String
index|[]
block|{
literal|"six"
block|,
literal|"six"
block|,
literal|"drunken"
block|,
literal|"drunken"
block|}
decl_stmt|;
DECL|field|starts
specifier|private
specifier|final
name|int
name|starts
index|[]
init|=
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|4
block|,
literal|4
block|}
decl_stmt|;
DECL|field|ends
specifier|private
specifier|final
name|int
name|ends
index|[]
init|=
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|3
block|,
literal|11
block|,
literal|11
block|}
decl_stmt|;
DECL|field|incs
specifier|private
specifier|final
name|int
name|incs
index|[]
init|=
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|}
decl_stmt|;
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|nextTokenIndex
operator|<
name|tokenCount
condition|)
block|{
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|terms
index|[
name|nextTokenIndex
index|]
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|starts
index|[
name|nextTokenIndex
index|]
argument_list|,
name|ends
index|[
name|nextTokenIndex
index|]
argument_list|)
expr_stmt|;
name|posIncAtt
operator|.
name|setPositionIncrement
argument_list|(
name|incs
index|[
name|nextTokenIndex
index|]
argument_list|)
expr_stmt|;
name|nextTokenIndex
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|this
operator|.
name|nextTokenIndex
operator|=
literal|0
expr_stmt|;
block|}
block|}
end_class
end_unit
