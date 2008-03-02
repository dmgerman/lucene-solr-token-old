begin_unit
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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
name|File
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexWriter
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
name|index
operator|.
name|MergeScheduler
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
name|index
operator|.
name|ConcurrentMergeScheduler
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
name|index
operator|.
name|CheckIndex
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
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import
begin_class
DECL|class|_TestUtil
specifier|public
class|class
name|_TestUtil
block|{
DECL|method|rmDir
specifier|public
specifier|static
name|void
name|rmDir
parameter_list|(
name|File
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|dir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|File
index|[]
name|files
init|=
name|dir
operator|.
name|listFiles
argument_list|()
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|files
index|[
name|i
index|]
operator|.
name|delete
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"could not delete "
operator|+
name|files
index|[
name|i
index|]
argument_list|)
throw|;
block|}
block|}
name|dir
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|rmDir
specifier|public
specifier|static
name|void
name|rmDir
parameter_list|(
name|String
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|rmDir
argument_list|(
operator|new
name|File
argument_list|(
name|dir
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|syncConcurrentMerges
specifier|public
specifier|static
name|void
name|syncConcurrentMerges
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
block|{
name|syncConcurrentMerges
argument_list|(
name|writer
operator|.
name|getMergeScheduler
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|syncConcurrentMerges
specifier|public
specifier|static
name|void
name|syncConcurrentMerges
parameter_list|(
name|MergeScheduler
name|ms
parameter_list|)
block|{
if|if
condition|(
name|ms
operator|instanceof
name|ConcurrentMergeScheduler
condition|)
operator|(
operator|(
name|ConcurrentMergeScheduler
operator|)
name|ms
operator|)
operator|.
name|sync
argument_list|()
expr_stmt|;
block|}
DECL|method|checkIndex
specifier|public
specifier|static
name|boolean
name|checkIndex
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
literal|1024
argument_list|)
decl_stmt|;
name|CheckIndex
operator|.
name|out
operator|=
operator|new
name|PrintStream
argument_list|(
name|bos
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|CheckIndex
operator|.
name|check
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"CheckIndex failed"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|bos
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
else|else
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
