begin_unit
begin_package
DECL|package|org.apache.lucene.misc
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|misc
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|FSDirectory
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
name|DirectoryReader
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
name|IndexReader
import|;
end_import
begin_comment
comment|/**  * Utility to get document frequency and total number of occurrences (sum of the tf for each doc)  of a term.   */
end_comment
begin_class
DECL|class|GetTermInfo
specifier|public
class|class
name|GetTermInfo
block|{
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|FSDirectory
name|dir
init|=
literal|null
decl_stmt|;
name|String
name|inputStr
init|=
literal|null
decl_stmt|;
name|String
name|field
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|3
condition|)
block|{
name|dir
operator|=
name|FSDirectory
operator|.
name|open
argument_list|(
operator|new
name|File
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|field
operator|=
name|args
index|[
literal|1
index|]
expr_stmt|;
name|inputStr
operator|=
name|args
index|[
literal|2
index|]
expr_stmt|;
block|}
else|else
block|{
name|usage
argument_list|()
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|getTermInfo
argument_list|(
name|dir
argument_list|,
name|field
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|inputStr
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getTermInfo
specifier|public
specifier|static
name|void
name|getTermInfo
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|field
parameter_list|,
name|BytesRef
name|termtext
parameter_list|)
throws|throws
name|Exception
block|{
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|long
name|totalTF
init|=
name|HighFreqTerms
operator|.
name|getTotalTermFreq
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|termtext
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"%s:%s \t totalTF = %,d \t doc freq = %,d \n"
argument_list|,
name|field
argument_list|,
name|termtext
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|totalTF
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
name|field
argument_list|,
name|termtext
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|usage
specifier|private
specifier|static
name|void
name|usage
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\n\nusage:\n\t"
operator|+
literal|"java "
operator|+
name|GetTermInfo
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"<index dir> field term \n\n"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
