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
name|EOFException
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
name|util
operator|.
name|Collections
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
name|store
operator|.
name|BaseDirectoryWrapper
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
name|store
operator|.
name|IndexOutput
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
name|LineFileDocs
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
name|TestUtil
import|;
end_import
begin_comment
comment|/**  * Test that a plain default detects broken index headers early (on opening a reader).  */
end_comment
begin_class
DECL|class|TestAllFilesCheckIndexHeader
specifier|public
class|class
name|TestAllFilesCheckIndexHeader
extends|extends
name|LuceneTestCase
block|{
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
if|if
condition|(
name|dir
operator|instanceof
name|MockDirectoryWrapper
condition|)
block|{
comment|// otherwise we can have unref'd files left in the index that won't be visited when opening a reader and lead to scary looking false failures:
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|dir
operator|)
operator|.
name|setEnableVirusScanner
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|IndexWriterConfig
name|conf
init|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setCodec
argument_list|(
name|TestUtil
operator|.
name|getDefaultCodec
argument_list|()
argument_list|)
expr_stmt|;
comment|// Disable CFS 80% of the time so we can truncate individual files, but the other 20% of the time we test truncation of .cfs/.cfe too:
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|!=
literal|1
condition|)
block|{
name|conf
operator|.
name|setUseCompoundFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|getMergePolicy
argument_list|()
operator|.
name|setNoCFSRatio
argument_list|(
literal|0.0
argument_list|)
expr_stmt|;
block|}
name|RandomIndexWriter
name|riw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
comment|// Use LineFileDocs so we (hopefully) get most Lucene features
comment|// tested, e.g. IntPoint was recently added to it:
name|LineFileDocs
name|docs
init|=
operator|new
name|LineFileDocs
argument_list|(
name|random
argument_list|()
argument_list|)
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|riw
operator|.
name|addDocument
argument_list|(
name|docs
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|7
argument_list|)
operator|==
literal|0
condition|)
block|{
name|riw
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
operator|==
literal|0
condition|)
block|{
name|riw
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"docid"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|15
argument_list|)
operator|==
literal|0
condition|)
block|{
name|riw
operator|.
name|updateNumericDocValue
argument_list|(
operator|new
name|Term
argument_list|(
literal|"docid"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|,
literal|"docid_intDV"
argument_list|,
name|Long
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|TEST_NIGHTLY
operator|==
literal|false
condition|)
block|{
name|riw
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|riw
operator|.
name|close
argument_list|()
expr_stmt|;
name|checkIndexHeader
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|checkIndexHeader
specifier|private
name|void
name|checkIndexHeader
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|String
name|name
range|:
name|dir
operator|.
name|listAll
argument_list|()
control|)
block|{
name|checkOneFile
argument_list|(
name|dir
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkOneFile
specifier|private
name|void
name|checkOneFile
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|victim
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|BaseDirectoryWrapper
name|dirCopy
init|=
name|newDirectory
argument_list|()
init|)
block|{
name|dirCopy
operator|.
name|setCheckIndexOnClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|long
name|victimLength
init|=
name|dir
operator|.
name|fileLength
argument_list|(
name|victim
argument_list|)
decl_stmt|;
name|int
name|wrongBytes
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
literal|100
argument_list|,
name|victimLength
argument_list|)
argument_list|)
decl_stmt|;
assert|assert
name|victimLength
operator|>
literal|0
assert|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: now break file "
operator|+
name|victim
operator|+
literal|" by randomizing first "
operator|+
name|wrongBytes
operator|+
literal|" of "
operator|+
name|victimLength
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|name
range|:
name|dir
operator|.
name|listAll
argument_list|()
control|)
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|victim
argument_list|)
operator|==
literal|false
condition|)
block|{
name|dirCopy
operator|.
name|copyFrom
argument_list|(
name|dir
argument_list|,
name|name
argument_list|,
name|name
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
init|(
name|IndexOutput
name|out
init|=
name|dirCopy
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
init|;               IndexInput in = dir.openInput(name
operator|,
init|IOContext.DEFAULT)
block|)
block|{
comment|// keeps same file length, but replaces the first wrongBytes with random bytes:
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|wrongBytes
index|]
decl_stmt|;
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|in
operator|.
name|seek
argument_list|(
name|wrongBytes
argument_list|)
expr_stmt|;
name|out
operator|.
name|copyBytes
argument_list|(
name|in
argument_list|,
name|victimLength
operator|-
name|wrongBytes
argument_list|)
expr_stmt|;
block|}
block|}
name|dirCopy
operator|.
name|sync
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
comment|// NOTE: we .close so that if the test fails (truncation not detected) we don't also get all these confusing errors about open files:
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dirCopy
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"wrong bytes not detected after randomizing first "
operator|+
name|wrongBytes
operator|+
literal|" bytes out of "
operator|+
name|victimLength
operator|+
literal|" for file "
operator|+
name|victim
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CorruptIndexException
decl||
name|EOFException
decl||
name|IndexFormatTooOldException
name|e
parameter_list|)
block|{
comment|// expected
block|}
comment|// CheckIndex should also fail:
try|try
block|{
name|TestUtil
operator|.
name|checkIndex
argument_list|(
name|dirCopy
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"wrong bytes not detected after randomizing first "
operator|+
name|wrongBytes
operator|+
literal|" bytes out of "
operator|+
name|victimLength
operator|+
literal|" for file "
operator|+
name|victim
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CorruptIndexException
decl||
name|EOFException
decl||
name|IndexFormatTooOldException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
block|}
end_class
unit|}
end_unit
