begin_unit
begin_package
DECL|package|org.apache.lucene.index.codecs.appending
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|codecs
operator|.
name|appending
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|FieldInfos
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
name|codecs
operator|.
name|PostingsReaderBase
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
name|codecs
operator|.
name|BlockTermsReader
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
name|codecs
operator|.
name|BlockTermsWriter
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
name|codecs
operator|.
name|TermsIndexReaderBase
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
name|store
operator|.
name|IndexInput
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
name|CodecUtil
import|;
end_import
begin_class
DECL|class|AppendingTermsDictReader
specifier|public
class|class
name|AppendingTermsDictReader
extends|extends
name|BlockTermsReader
block|{
DECL|method|AppendingTermsDictReader
specifier|public
name|AppendingTermsDictReader
parameter_list|(
name|TermsIndexReaderBase
name|indexReader
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|,
name|String
name|segment
parameter_list|,
name|PostingsReaderBase
name|postingsReader
parameter_list|,
name|IOContext
name|context
parameter_list|,
name|int
name|termsCacheSize
parameter_list|,
name|int
name|codecId
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|indexReader
argument_list|,
name|dir
argument_list|,
name|fieldInfos
argument_list|,
name|segment
argument_list|,
name|postingsReader
argument_list|,
name|context
argument_list|,
name|termsCacheSize
argument_list|,
name|codecId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readHeader
specifier|protected
name|void
name|readHeader
parameter_list|(
name|IndexInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|in
argument_list|,
name|AppendingTermsDictWriter
operator|.
name|CODEC_NAME
argument_list|,
name|BlockTermsWriter
operator|.
name|VERSION_START
argument_list|,
name|BlockTermsWriter
operator|.
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|seekDir
specifier|protected
name|void
name|seekDir
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|long
name|dirOffset
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|.
name|seek
argument_list|(
name|in
operator|.
name|length
argument_list|()
operator|-
name|Long
operator|.
name|SIZE
operator|/
literal|8
argument_list|)
expr_stmt|;
name|long
name|offset
init|=
name|in
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|in
operator|.
name|seek
argument_list|(
name|offset
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
