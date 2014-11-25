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
name|nio
operator|.
name|file
operator|.
name|FileSystem
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
name|Path
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
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import
begin_comment
comment|/**   * FileSystem that tracks open handles.  *<p>  * When {@link FileSystem#close()} is called, this class will throw  * an exception if any file handles are still open.  */
end_comment
begin_class
DECL|class|LeakFS
specifier|public
class|class
name|LeakFS
extends|extends
name|HandleTrackingFS
block|{
comment|// we explicitly use reference hashcode/equality in our keys
DECL|field|openHandles
specifier|private
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|Exception
argument_list|>
name|openHandles
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * Create a new instance, tracking file handle leaks for the     * specified delegate filesystem.    * @param delegate delegate filesystem to wrap.    */
DECL|method|LeakFS
specifier|public
name|LeakFS
parameter_list|(
name|FileSystem
name|delegate
parameter_list|)
block|{
name|super
argument_list|(
literal|"leakfs://"
argument_list|,
name|delegate
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onOpen
specifier|protected
name|void
name|onOpen
parameter_list|(
name|Path
name|path
parameter_list|,
name|Object
name|stream
parameter_list|)
block|{
name|openHandles
operator|.
name|put
argument_list|(
name|stream
argument_list|,
operator|new
name|Exception
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onClose
specifier|protected
name|void
name|onClose
parameter_list|(
name|Path
name|path
parameter_list|,
name|Object
name|stream
parameter_list|)
block|{
name|openHandles
operator|.
name|remove
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onClose
specifier|public
specifier|synchronized
name|void
name|onClose
parameter_list|()
block|{
if|if
condition|(
operator|!
name|openHandles
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// print the first one as its very verbose otherwise
name|Exception
name|cause
init|=
literal|null
decl_stmt|;
name|Iterator
argument_list|<
name|Exception
argument_list|>
name|stacktraces
init|=
name|openHandles
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|stacktraces
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|cause
operator|=
name|stacktraces
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"file handle leaks: "
operator|+
name|openHandles
operator|.
name|keySet
argument_list|()
argument_list|,
name|cause
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit
