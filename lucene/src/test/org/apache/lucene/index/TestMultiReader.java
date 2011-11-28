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
name|store
operator|.
name|MockDirectoryWrapper
import|;
end_import
begin_class
DECL|class|TestMultiReader
specifier|public
class|class
name|TestMultiReader
extends|extends
name|TestDirectoryReader
block|{
comment|// TODO: files are never fsynced if you do what this test is doing,
comment|// so the checkindex is disabled.
annotation|@
name|Override
DECL|method|createDirectory
specifier|protected
name|Directory
name|createDirectory
parameter_list|()
throws|throws
name|IOException
block|{
name|MockDirectoryWrapper
name|mdw
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|mdw
operator|.
name|setCheckIndexOnClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|mdw
return|;
block|}
annotation|@
name|Override
DECL|method|openReader
specifier|protected
name|IndexReader
name|openReader
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexReader
name|reader
decl_stmt|;
name|sis
operator|.
name|read
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|SegmentReader
name|reader1
init|=
name|SegmentReader
operator|.
name|get
argument_list|(
literal|false
argument_list|,
name|sis
operator|.
name|info
argument_list|(
literal|0
argument_list|)
argument_list|,
name|IndexReader
operator|.
name|DEFAULT_TERMS_INDEX_DIVISOR
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|)
argument_list|)
decl_stmt|;
name|SegmentReader
name|reader2
init|=
name|SegmentReader
operator|.
name|get
argument_list|(
literal|false
argument_list|,
name|sis
operator|.
name|info
argument_list|(
literal|1
argument_list|)
argument_list|,
name|IndexReader
operator|.
name|DEFAULT_TERMS_INDEX_DIVISOR
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|)
argument_list|)
decl_stmt|;
name|readers
index|[
literal|0
index|]
operator|=
name|reader1
expr_stmt|;
name|readers
index|[
literal|1
index|]
operator|=
name|reader2
expr_stmt|;
name|assertTrue
argument_list|(
name|reader1
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reader2
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|reader
operator|=
operator|new
name|MultiReader
argument_list|(
name|readers
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dir
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|sis
operator|!=
literal|null
argument_list|)
expr_stmt|;
return|return
name|reader
return|;
block|}
block|}
end_class
end_unit
