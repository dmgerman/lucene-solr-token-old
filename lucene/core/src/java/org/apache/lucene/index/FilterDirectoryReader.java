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
name|util
operator|.
name|List
import|;
end_import
begin_comment
comment|/**  * A FilterDirectoryReader wraps another DirectoryReader, allowing implementations  * to transform or extend it.  *  * Subclasses should implement doWrapDirectoryReader to return an instance of the  * subclass.  *  * If the subclass wants to wrap the DirectoryReader's subreaders, it should also  * implement a SubReaderWrapper subclass, and pass an instance to its super  * constructor.  */
end_comment
begin_class
DECL|class|FilterDirectoryReader
specifier|public
specifier|abstract
class|class
name|FilterDirectoryReader
extends|extends
name|DirectoryReader
block|{
comment|/** Get the wrapped instance by<code>reader</code> as long as this reader is    *  an instance of {@link FilterDirectoryReader}.  */
DECL|method|unwrap
specifier|public
specifier|static
name|DirectoryReader
name|unwrap
parameter_list|(
name|DirectoryReader
name|reader
parameter_list|)
block|{
while|while
condition|(
name|reader
operator|instanceof
name|FilterDirectoryReader
condition|)
block|{
name|reader
operator|=
operator|(
operator|(
name|FilterDirectoryReader
operator|)
name|reader
operator|)
operator|.
name|in
expr_stmt|;
block|}
return|return
name|reader
return|;
block|}
comment|/**    * Factory class passed to FilterDirectoryReader constructor that allows    * subclasses to wrap the filtered DirectoryReader's subreaders.  You    * can use this to, e.g., wrap the subreaders with specialised    * FilterLeafReader implementations.    */
DECL|class|SubReaderWrapper
specifier|public
specifier|static
specifier|abstract
class|class
name|SubReaderWrapper
block|{
DECL|method|wrap
specifier|private
name|LeafReader
index|[]
name|wrap
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|LeafReader
argument_list|>
name|readers
parameter_list|)
block|{
name|LeafReader
index|[]
name|wrapped
init|=
operator|new
name|LeafReader
index|[
name|readers
operator|.
name|size
argument_list|()
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
name|readers
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|wrapped
index|[
name|i
index|]
operator|=
name|wrap
argument_list|(
name|readers
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|wrapped
return|;
block|}
comment|/** Constructor */
DECL|method|SubReaderWrapper
specifier|public
name|SubReaderWrapper
parameter_list|()
block|{}
comment|/**      * Wrap one of the parent DirectoryReader's subreaders      * @param reader the subreader to wrap      * @return a wrapped/filtered LeafReader      */
DECL|method|wrap
specifier|public
specifier|abstract
name|LeafReader
name|wrap
parameter_list|(
name|LeafReader
name|reader
parameter_list|)
function_decl|;
block|}
comment|/** The filtered DirectoryReader */
DECL|field|in
specifier|protected
specifier|final
name|DirectoryReader
name|in
decl_stmt|;
comment|/**    * Create a new FilterDirectoryReader that filters a passed in DirectoryReader,    * using the supplied SubReaderWrapper to wrap its subreader.    * @param in the DirectoryReader to filter    * @param wrapper the SubReaderWrapper to use to wrap subreaders    */
DECL|method|FilterDirectoryReader
specifier|public
name|FilterDirectoryReader
parameter_list|(
name|DirectoryReader
name|in
parameter_list|,
name|SubReaderWrapper
name|wrapper
parameter_list|)
block|{
name|super
argument_list|(
name|in
operator|.
name|directory
argument_list|()
argument_list|,
name|wrapper
operator|.
name|wrap
argument_list|(
name|in
operator|.
name|getSequentialSubReaders
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
comment|/**    * Called by the doOpenIfChanged() methods to return a new wrapped DirectoryReader.    *    * Implementations should just return an instantiation of themselves, wrapping the    * passed in DirectoryReader.    *    * @param in the DirectoryReader to wrap    * @return the wrapped DirectoryReader    */
DECL|method|doWrapDirectoryReader
specifier|protected
specifier|abstract
name|DirectoryReader
name|doWrapDirectoryReader
parameter_list|(
name|DirectoryReader
name|in
parameter_list|)
function_decl|;
DECL|method|wrapDirectoryReader
specifier|private
specifier|final
name|DirectoryReader
name|wrapDirectoryReader
parameter_list|(
name|DirectoryReader
name|in
parameter_list|)
block|{
return|return
name|in
operator|==
literal|null
condition|?
literal|null
else|:
name|doWrapDirectoryReader
argument_list|(
name|in
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doOpenIfChanged
specifier|protected
specifier|final
name|DirectoryReader
name|doOpenIfChanged
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|wrapDirectoryReader
argument_list|(
name|in
operator|.
name|doOpenIfChanged
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doOpenIfChanged
specifier|protected
specifier|final
name|DirectoryReader
name|doOpenIfChanged
parameter_list|(
name|IndexCommit
name|commit
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|wrapDirectoryReader
argument_list|(
name|in
operator|.
name|doOpenIfChanged
argument_list|(
name|commit
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doOpenIfChanged
specifier|protected
specifier|final
name|DirectoryReader
name|doOpenIfChanged
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|boolean
name|applyAllDeletes
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|wrapDirectoryReader
argument_list|(
name|in
operator|.
name|doOpenIfChanged
argument_list|(
name|writer
argument_list|,
name|applyAllDeletes
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|long
name|getVersion
parameter_list|()
block|{
return|return
name|in
operator|.
name|getVersion
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isCurrent
specifier|public
name|boolean
name|isCurrent
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|isCurrent
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getIndexCommit
specifier|public
name|IndexCommit
name|getIndexCommit
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|getIndexCommit
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|doClose
argument_list|()
expr_stmt|;
block|}
comment|/** Returns the wrapped {@link DirectoryReader}. */
DECL|method|getDelegate
specifier|public
name|DirectoryReader
name|getDelegate
parameter_list|()
block|{
return|return
name|in
return|;
block|}
block|}
end_class
end_unit
