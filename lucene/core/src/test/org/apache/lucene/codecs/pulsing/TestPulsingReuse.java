begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.pulsing
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|pulsing
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|IdentityHashMap
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|MockAnalyzer
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
name|Codec
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
name|nestedpulsing
operator|.
name|NestedPulsingPostingsFormat
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|document
operator|.
name|TextField
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
name|AtomicReader
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
name|DocsAndPositionsEnum
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
name|DocsEnum
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
name|RandomIndexWriter
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
name|TermsEnum
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
name|MockDirectoryWrapper
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
name|LuceneTestCase
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
name|_TestUtil
import|;
end_import
begin_comment
comment|/**  * Tests that pulsing codec reuses its enums and wrapped enums  */
end_comment
begin_class
DECL|class|TestPulsingReuse
specifier|public
class|class
name|TestPulsingReuse
extends|extends
name|LuceneTestCase
block|{
comment|// TODO: this is a basic test. this thing is complicated, add more
DECL|method|testSophisticatedReuse
specifier|public
name|void
name|testSophisticatedReuse
parameter_list|()
throws|throws
name|Exception
block|{
comment|// we always run this test with pulsing codec.
name|Codec
name|cp
init|=
name|_TestUtil
operator|.
name|alwaysPostingsFormat
argument_list|(
operator|new
name|Pulsing40PostingsFormat
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setCodec
argument_list|(
name|cp
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"foo"
argument_list|,
literal|"a b b c c c d e f g g h i i j j k"
argument_list|,
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|DirectoryReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|AtomicReader
name|segment
init|=
name|getOnlySegmentReader
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|DocsEnum
name|reuse
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|DocsEnum
argument_list|,
name|Boolean
argument_list|>
name|allEnums
init|=
operator|new
name|IdentityHashMap
argument_list|<
name|DocsEnum
argument_list|,
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
name|TermsEnum
name|te
init|=
name|segment
operator|.
name|terms
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
while|while
condition|(
name|te
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|reuse
operator|=
name|te
operator|.
name|docs
argument_list|(
literal|null
argument_list|,
name|reuse
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|allEnums
operator|.
name|put
argument_list|(
name|reuse
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|allEnums
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|allEnums
operator|.
name|clear
argument_list|()
expr_stmt|;
name|DocsAndPositionsEnum
name|posReuse
init|=
literal|null
decl_stmt|;
name|te
operator|=
name|segment
operator|.
name|terms
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
expr_stmt|;
while|while
condition|(
name|te
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|posReuse
operator|=
name|te
operator|.
name|docsAndPositions
argument_list|(
literal|null
argument_list|,
name|posReuse
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|allEnums
operator|.
name|put
argument_list|(
name|posReuse
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|allEnums
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** tests reuse with Pulsing1(Pulsing2(Standard)) */
DECL|method|testNestedPulsing
specifier|public
name|void
name|testNestedPulsing
parameter_list|()
throws|throws
name|Exception
block|{
comment|// we always run this test with pulsing codec.
name|Codec
name|cp
init|=
name|_TestUtil
operator|.
name|alwaysPostingsFormat
argument_list|(
operator|new
name|NestedPulsingPostingsFormat
argument_list|()
argument_list|)
decl_stmt|;
name|MockDirectoryWrapper
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|dir
operator|.
name|setCheckIndexOnClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// will do this ourselves, custom codec
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setCodec
argument_list|(
name|cp
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"foo"
argument_list|,
literal|"a b b c c c d e f g g g h i i j j k l l m m m"
argument_list|,
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
argument_list|)
expr_stmt|;
comment|// note: the reuse is imperfect, here we would have 4 enums (lost reuse when we get an enum for 'm')
comment|// this is because we only track the 'last' enum we reused (not all).
comment|// but this seems 'good enough' for now.
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|DirectoryReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|AtomicReader
name|segment
init|=
name|getOnlySegmentReader
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|DocsEnum
name|reuse
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|DocsEnum
argument_list|,
name|Boolean
argument_list|>
name|allEnums
init|=
operator|new
name|IdentityHashMap
argument_list|<
name|DocsEnum
argument_list|,
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
name|TermsEnum
name|te
init|=
name|segment
operator|.
name|terms
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
while|while
condition|(
name|te
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|reuse
operator|=
name|te
operator|.
name|docs
argument_list|(
literal|null
argument_list|,
name|reuse
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|allEnums
operator|.
name|put
argument_list|(
name|reuse
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|allEnums
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|allEnums
operator|.
name|clear
argument_list|()
expr_stmt|;
name|DocsAndPositionsEnum
name|posReuse
init|=
literal|null
decl_stmt|;
name|te
operator|=
name|segment
operator|.
name|terms
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
expr_stmt|;
while|while
condition|(
name|te
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|posReuse
operator|=
name|te
operator|.
name|docsAndPositions
argument_list|(
literal|null
argument_list|,
name|posReuse
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|allEnums
operator|.
name|put
argument_list|(
name|posReuse
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|allEnums
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|CheckIndex
name|ci
init|=
operator|new
name|CheckIndex
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|ci
operator|.
name|checkIndex
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
