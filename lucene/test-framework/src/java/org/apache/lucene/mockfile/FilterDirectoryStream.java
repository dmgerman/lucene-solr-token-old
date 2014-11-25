begin_unit
begin_package
DECL|package|org.apache.lucene.mockfile
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|mockfile
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
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|DirectoryStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import
begin_comment
comment|/**    * A {@code FilterDirectoryStream} contains another   * {@code DirectoryStream}, which it uses as its basic   * source of data, possibly transforming the data along the   * way or providing additional functionality.   */
end_comment
begin_class
DECL|class|FilterDirectoryStream
specifier|public
class|class
name|FilterDirectoryStream
parameter_list|<
name|T
parameter_list|>
implements|implements
name|DirectoryStream
argument_list|<
name|T
argument_list|>
block|{
comment|/**     * The underlying {@code DirectoryStream} instance.     */
DECL|field|delegate
specifier|protected
specifier|final
name|DirectoryStream
argument_list|<
name|T
argument_list|>
name|delegate
decl_stmt|;
comment|/**    * Construct a {@code FilterDirectoryStream} based on     * the specified base stream.    *<p>    * Note that base stream is closed if this stream is closed.    * @param delegate specified base stream.    */
DECL|method|FilterDirectoryStream
specifier|public
name|FilterDirectoryStream
parameter_list|(
name|DirectoryStream
argument_list|<
name|T
argument_list|>
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|delegate
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|T
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
end_class
end_unit
