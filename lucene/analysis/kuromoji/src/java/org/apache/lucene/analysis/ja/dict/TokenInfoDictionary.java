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
name|InputStream
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
name|IOUtils
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
name|fst
operator|.
name|FST
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
name|fst
operator|.
name|PositiveIntOutputs
import|;
end_import
begin_comment
comment|/**  * Binary dictionary implementation for a known-word dictionary model:  * Words are encoded into an FST mapping to a list of wordIDs.  */
end_comment
begin_class
DECL|class|TokenInfoDictionary
specifier|public
specifier|final
class|class
name|TokenInfoDictionary
extends|extends
name|BinaryDictionary
block|{
DECL|field|FST_FILENAME_SUFFIX
specifier|public
specifier|static
specifier|final
name|String
name|FST_FILENAME_SUFFIX
init|=
literal|"$fst.dat"
decl_stmt|;
DECL|field|fst
specifier|private
specifier|final
name|TokenInfoFST
name|fst
decl_stmt|;
DECL|method|TokenInfoDictionary
specifier|private
name|TokenInfoDictionary
parameter_list|()
throws|throws
name|IOException
block|{
name|super
argument_list|()
expr_stmt|;
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
name|FST
argument_list|<
name|Long
argument_list|>
name|fst
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
name|getResource
argument_list|(
name|FST_FILENAME_SUFFIX
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
name|fst
operator|=
operator|new
name|FST
argument_list|<>
argument_list|(
operator|new
name|InputStreamDataInput
argument_list|(
name|is
argument_list|)
argument_list|,
name|PositiveIntOutputs
operator|.
name|getSingleton
argument_list|()
argument_list|)
expr_stmt|;
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
comment|// TODO: some way to configure?
name|this
operator|.
name|fst
operator|=
operator|new
name|TokenInfoFST
argument_list|(
name|fst
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|getFST
specifier|public
name|TokenInfoFST
name|getFST
parameter_list|()
block|{
return|return
name|fst
return|;
block|}
DECL|method|getInstance
specifier|public
specifier|static
name|TokenInfoDictionary
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
name|TokenInfoDictionary
name|INSTANCE
decl_stmt|;
static|static
block|{
try|try
block|{
name|INSTANCE
operator|=
operator|new
name|TokenInfoDictionary
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
literal|"Cannot load TokenInfoDictionary."
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
