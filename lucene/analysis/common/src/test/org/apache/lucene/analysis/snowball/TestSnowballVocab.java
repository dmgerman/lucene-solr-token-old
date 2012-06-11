begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.snowball
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|snowball
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
name|io
operator|.
name|Reader
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
name|Analyzer
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
name|Tokenizer
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
name|core
operator|.
name|KeywordTokenizer
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
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|VocabularyAssert
operator|.
name|*
import|;
end_import
begin_comment
comment|/**  * Test the snowball filters against the snowball data tests  */
end_comment
begin_class
DECL|class|TestSnowballVocab
specifier|public
class|class
name|TestSnowballVocab
extends|extends
name|LuceneTestCase
block|{
comment|/**    * Run all languages against their snowball vocabulary tests.    */
DECL|method|testStemmers
specifier|public
name|void
name|testStemmers
parameter_list|()
throws|throws
name|IOException
block|{
name|assertCorrectOutput
argument_list|(
literal|"Danish"
argument_list|,
literal|"danish"
argument_list|)
expr_stmt|;
name|assertCorrectOutput
argument_list|(
literal|"Dutch"
argument_list|,
literal|"dutch"
argument_list|)
expr_stmt|;
name|assertCorrectOutput
argument_list|(
literal|"English"
argument_list|,
literal|"english"
argument_list|)
expr_stmt|;
comment|// disabled due to snowball java code generation bug:
comment|// see http://article.gmane.org/gmane.comp.search.snowball/1139
comment|// assertCorrectOutput("Finnish", "finnish");
name|assertCorrectOutput
argument_list|(
literal|"French"
argument_list|,
literal|"french"
argument_list|)
expr_stmt|;
name|assertCorrectOutput
argument_list|(
literal|"German"
argument_list|,
literal|"german"
argument_list|)
expr_stmt|;
name|assertCorrectOutput
argument_list|(
literal|"German2"
argument_list|,
literal|"german2"
argument_list|)
expr_stmt|;
name|assertCorrectOutput
argument_list|(
literal|"Hungarian"
argument_list|,
literal|"hungarian"
argument_list|)
expr_stmt|;
name|assertCorrectOutput
argument_list|(
literal|"Italian"
argument_list|,
literal|"italian"
argument_list|)
expr_stmt|;
name|assertCorrectOutput
argument_list|(
literal|"Kp"
argument_list|,
literal|"kraaij_pohlmann"
argument_list|)
expr_stmt|;
comment|// disabled due to snowball java code generation bug:
comment|// see http://article.gmane.org/gmane.comp.search.snowball/1139
comment|// assertCorrectOutput("Lovins", "lovins");
name|assertCorrectOutput
argument_list|(
literal|"Norwegian"
argument_list|,
literal|"norwegian"
argument_list|)
expr_stmt|;
name|assertCorrectOutput
argument_list|(
literal|"Porter"
argument_list|,
literal|"porter"
argument_list|)
expr_stmt|;
name|assertCorrectOutput
argument_list|(
literal|"Portuguese"
argument_list|,
literal|"portuguese"
argument_list|)
expr_stmt|;
name|assertCorrectOutput
argument_list|(
literal|"Romanian"
argument_list|,
literal|"romanian"
argument_list|)
expr_stmt|;
name|assertCorrectOutput
argument_list|(
literal|"Russian"
argument_list|,
literal|"russian"
argument_list|)
expr_stmt|;
name|assertCorrectOutput
argument_list|(
literal|"Spanish"
argument_list|,
literal|"spanish"
argument_list|)
expr_stmt|;
name|assertCorrectOutput
argument_list|(
literal|"Swedish"
argument_list|,
literal|"swedish"
argument_list|)
expr_stmt|;
name|assertCorrectOutput
argument_list|(
literal|"Turkish"
argument_list|,
literal|"turkish"
argument_list|)
expr_stmt|;
block|}
comment|/**    * For the supplied language, run the stemmer against all strings in voc.txt    * The output should be the same as the string in output.txt    */
DECL|method|assertCorrectOutput
specifier|private
name|void
name|assertCorrectOutput
parameter_list|(
specifier|final
name|String
name|snowballLanguage
parameter_list|,
name|String
name|dataDirectory
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"checking snowball language: "
operator|+
name|snowballLanguage
argument_list|)
expr_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|Tokenizer
name|t
init|=
operator|new
name|KeywordTokenizer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|t
argument_list|,
operator|new
name|SnowballFilter
argument_list|(
name|t
argument_list|,
name|snowballLanguage
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|assertVocabulary
argument_list|(
name|a
argument_list|,
name|getDataFile
argument_list|(
literal|"TestSnowballVocabData.zip"
argument_list|)
argument_list|,
name|dataDirectory
operator|+
literal|"/voc.txt"
argument_list|,
name|dataDirectory
operator|+
literal|"/output.txt"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
