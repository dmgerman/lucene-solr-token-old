begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
operator|.
name|ReaderContext
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
name|DocIdBitSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|BitSet
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
begin_class
DECL|class|SingleDocTestFilter
specifier|public
class|class
name|SingleDocTestFilter
extends|extends
name|Filter
block|{
DECL|field|doc
specifier|private
name|int
name|doc
decl_stmt|;
DECL|method|SingleDocTestFilter
specifier|public
name|SingleDocTestFilter
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|this
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|ReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|BitSet
name|bits
init|=
operator|new
name|BitSet
argument_list|(
name|context
operator|.
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
name|bits
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
return|return
operator|new
name|DocIdBitSet
argument_list|(
name|bits
argument_list|)
return|;
block|}
block|}
end_class
end_unit
