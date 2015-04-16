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
name|InputStream
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
comment|// LUCENE-6382
end_comment
begin_class
DECL|class|TestMaxPositionInOldIndex
specifier|public
class|class
name|TestMaxPositionInOldIndex
extends|extends
name|LuceneTestCase
block|{
comment|// Save this to BuildMaxPositionIndex.java and follow the compile/run instructions to regenerate the .zip:
comment|/* import java.io.IOException; import java.nio.file.Paths;  import org.apache.lucene.analysis.CannedTokenStream; import org.apache.lucene.analysis.Token; import org.apache.lucene.analysis.core.WhitespaceAnalyzer; import org.apache.lucene.document.Document; import org.apache.lucene.document.TextField; import org.apache.lucene.index.IndexWriter; import org.apache.lucene.index.IndexWriterConfig; import org.apache.lucene.store.Directory; import org.apache.lucene.store.FSDirectory; import org.apache.lucene.util.BytesRef;  // Compile: //   javac -cp lucene/build/core/lucene-core-5.1.0-SNAPSHOT.jar:lucene/build/test-framework/lucene-test-framework-5.1.0-SNAPSHOT.jar:lucene/build/analysis/common/lucene-analyzers-common-5.1.0-SNAPSHOT.jar BuildMaxPositionIndex.java  // Run: //   java -cp .:lucene/build/core/lucene-core-5.1.0-SNAPSHOT.jar:lucene/build/test-framework/lucene-test-framework-5.1.0-SNAPSHOT.jar:lucene/build/analysis/common/lucene-analyzers-common-5.1.0-SNAPSHOT.jar:lucene/build/codecs/lucene-codecs-5.1.0-SNAPSHOT.jar BuildMaxPositionIndex  //  cd maxposindex //  zip maxposindex.zip *  public class BuildMaxPositionIndex {   public static void main(String[] args) throws IOException {     Directory dir = FSDirectory.open(Paths.get("maxposindex"));     IndexWriter iw = new IndexWriter(dir, new IndexWriterConfig(new WhitespaceAnalyzer()));     Document doc = new Document();     // This is at position 1:     Token t1 = new Token("foo", 0, 3);     t1.setPositionIncrement(2);     Token t2 = new Token("foo", 4, 7);     // This overflows max position:     t2.setPositionIncrement(Integer.MAX_VALUE-1);     t2.setPayload(new BytesRef(new byte[] { 0x1 } ));     doc.add(new TextField("foo", new CannedTokenStream(new Token[] {t1, t2})));     iw.addDocument(doc);     iw.close();     dir.close();   } }   */
DECL|method|testCorruptIndex
specifier|public
name|void
name|testCorruptIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|path
init|=
name|createTempDir
argument_list|(
literal|"maxposindex"
argument_list|)
decl_stmt|;
name|InputStream
name|resource
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"maxposindex.zip"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"maxposindex not found"
argument_list|,
name|resource
argument_list|)
expr_stmt|;
name|TestUtil
operator|.
name|unzip
argument_list|(
name|resource
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|BaseDirectoryWrapper
name|dir
init|=
name|newFSDirectory
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|dir
operator|.
name|setCheckIndexOnClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|TestUtil
operator|.
name|checkIndex
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"corruption was not detected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|re
parameter_list|)
block|{
comment|// expected
name|assertTrue
argument_list|(
name|re
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"pos 2147483647> IndexWriter.MAX_POSITION=2147483519"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Also confirm merging detects this:
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|()
decl_stmt|;
name|iwc
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|SerialMergeScheduler
argument_list|()
argument_list|)
expr_stmt|;
name|iwc
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CorruptIndexException
name|cie
parameter_list|)
block|{
comment|// SerialMergeScheduler
name|assertTrue
argument_list|(
literal|"got message "
operator|+
name|cie
operator|.
name|getMessage
argument_list|()
argument_list|,
name|cie
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"position=2147483647 is too large (> IndexWriter.MAX_POSITION=2147483519), field=\"foo\" doc=0 (resource=PerFieldPostings(segment=_0 formats=1)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|w
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
block|}
end_class
end_unit
