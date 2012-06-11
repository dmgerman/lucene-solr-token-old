begin_unit
begin_package
DECL|package|org.apache.lucene.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
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
name|Collection
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
name|SegmentInfoPerCommit
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
name|IOContext
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
name|Bits
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
name|MutableBits
import|;
end_import
begin_comment
comment|/** Format for live/deleted documents  * @lucene.experimental */
end_comment
begin_class
DECL|class|LiveDocsFormat
specifier|public
specifier|abstract
class|class
name|LiveDocsFormat
block|{
comment|/** Creates a new MutableBits, with all bits set, for the specified size. */
DECL|method|newLiveDocs
specifier|public
specifier|abstract
name|MutableBits
name|newLiveDocs
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Creates a new mutablebits of the same bits set and size of existing. */
DECL|method|newLiveDocs
specifier|public
specifier|abstract
name|MutableBits
name|newLiveDocs
parameter_list|(
name|Bits
name|existing
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Read live docs bits. */
DECL|method|readLiveDocs
specifier|public
specifier|abstract
name|Bits
name|readLiveDocs
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfoPerCommit
name|info
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Persist live docs bits.  Use {@link    *  SegmentInfoPerCommit#getNextDelGen} to determine the    *  generation of the deletes file you should write to. */
DECL|method|writeLiveDocs
specifier|public
specifier|abstract
name|void
name|writeLiveDocs
parameter_list|(
name|MutableBits
name|bits
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|SegmentInfoPerCommit
name|info
parameter_list|,
name|int
name|newDelCount
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Records all files in use by this {@link SegmentInfoPerCommit} into the files argument. */
DECL|method|files
specifier|public
specifier|abstract
name|void
name|files
parameter_list|(
name|SegmentInfoPerCommit
name|info
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class
end_unit
