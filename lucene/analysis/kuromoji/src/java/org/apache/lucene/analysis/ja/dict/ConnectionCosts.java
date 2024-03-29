begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.ja.dict
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ja
operator|.
name|dict
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
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
name|io
operator|.
name|InputStream
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
name|codecs
operator|.
name|CodecUtil
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
name|DataInput
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
name|InputStreamDataInput
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
name|BitUtil
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
name|IOUtils
import|;
end_import
begin_comment
comment|/**  * n-gram connection cost data  */
end_comment
begin_class
DECL|class|ConnectionCosts
specifier|public
specifier|final
class|class
name|ConnectionCosts
block|{
DECL|field|FILENAME_SUFFIX
specifier|public
specifier|static
specifier|final
name|String
name|FILENAME_SUFFIX
init|=
literal|".dat"
decl_stmt|;
DECL|field|HEADER
specifier|public
specifier|static
specifier|final
name|String
name|HEADER
init|=
literal|"kuromoji_cc"
decl_stmt|;
DECL|field|VERSION
specifier|public
specifier|static
specifier|final
name|int
name|VERSION
init|=
literal|1
decl_stmt|;
DECL|field|costs
specifier|private
specifier|final
name|short
index|[]
index|[]
name|costs
decl_stmt|;
comment|// array is backward IDs first since get is called using the same backward ID consecutively. maybe doesn't matter.
DECL|method|ConnectionCosts
specifier|private
name|ConnectionCosts
parameter_list|()
throws|throws
name|IOException
block|{
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
name|short
index|[]
index|[]
name|costs
init|=
literal|null
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|is
operator|=
name|BinaryDictionary
operator|.
name|getClassResource
argument_list|(
name|getClass
argument_list|()
argument_list|,
name|FILENAME_SUFFIX
argument_list|)
expr_stmt|;
name|is
operator|=
operator|new
name|BufferedInputStream
argument_list|(
name|is
argument_list|)
expr_stmt|;
specifier|final
name|DataInput
name|in
init|=
operator|new
name|InputStreamDataInput
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|in
argument_list|,
name|HEADER
argument_list|,
name|VERSION
argument_list|,
name|VERSION
argument_list|)
expr_stmt|;
name|int
name|forwardSize
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|int
name|backwardSize
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|costs
operator|=
operator|new
name|short
index|[
name|backwardSize
index|]
index|[
name|forwardSize
index|]
expr_stmt|;
name|int
name|accum
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|costs
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|short
index|[]
name|a
init|=
name|costs
index|[
name|j
index|]
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
name|a
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|accum
operator|+=
name|in
operator|.
name|readZInt
argument_list|()
expr_stmt|;
name|a
index|[
name|i
index|]
operator|=
operator|(
name|short
operator|)
name|accum
expr_stmt|;
block|}
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|costs
operator|=
name|costs
expr_stmt|;
block|}
DECL|method|get
specifier|public
name|int
name|get
parameter_list|(
name|int
name|forwardId
parameter_list|,
name|int
name|backwardId
parameter_list|)
block|{
return|return
name|costs
index|[
name|backwardId
index|]
index|[
name|forwardId
index|]
return|;
block|}
DECL|method|getInstance
specifier|public
specifier|static
name|ConnectionCosts
name|getInstance
parameter_list|()
block|{
return|return
name|SingletonHolder
operator|.
name|INSTANCE
return|;
block|}
DECL|class|SingletonHolder
specifier|private
specifier|static
class|class
name|SingletonHolder
block|{
DECL|field|INSTANCE
specifier|static
specifier|final
name|ConnectionCosts
name|INSTANCE
decl_stmt|;
static|static
block|{
try|try
block|{
name|INSTANCE
operator|=
operator|new
name|ConnectionCosts
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot load ConnectionCosts."
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class
end_unit
