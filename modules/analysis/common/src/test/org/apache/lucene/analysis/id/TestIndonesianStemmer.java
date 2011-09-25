begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.id
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|id
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|BaseTokenStreamTestCase
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
begin_comment
comment|/**  * Tests {@link IndonesianStemmer}  */
end_comment
begin_class
DECL|class|TestIndonesianStemmer
specifier|public
class|class
name|TestIndonesianStemmer
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|/* full stemming, no stopwords */
DECL|field|a
name|Analyzer
name|a
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
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
name|tokenizer
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
name|tokenizer
argument_list|,
operator|new
name|IndonesianStemFilter
argument_list|(
name|tokenizer
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/** Some examples from the paper */
DECL|method|testExamples
specifier|public
name|void
name|testExamples
parameter_list|()
throws|throws
name|IOException
block|{
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"bukukah"
argument_list|,
literal|"buku"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"adalah"
argument_list|,
literal|"ada"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"bukupun"
argument_list|,
literal|"buku"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"bukuku"
argument_list|,
literal|"buku"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"bukumu"
argument_list|,
literal|"buku"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"bukunya"
argument_list|,
literal|"buku"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"mengukur"
argument_list|,
literal|"ukur"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"menyapu"
argument_list|,
literal|"sapu"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"menduga"
argument_list|,
literal|"duga"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"menuduh"
argument_list|,
literal|"uduh"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"membaca"
argument_list|,
literal|"baca"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"merusak"
argument_list|,
literal|"rusak"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"pengukur"
argument_list|,
literal|"ukur"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"penyapu"
argument_list|,
literal|"sapu"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"penduga"
argument_list|,
literal|"duga"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"pembaca"
argument_list|,
literal|"baca"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"diukur"
argument_list|,
literal|"ukur"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"tersapu"
argument_list|,
literal|"sapu"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"kekasih"
argument_list|,
literal|"kasih"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"berlari"
argument_list|,
literal|"lari"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"belajar"
argument_list|,
literal|"ajar"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"bekerja"
argument_list|,
literal|"kerja"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"perjelas"
argument_list|,
literal|"jelas"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"pelajar"
argument_list|,
literal|"ajar"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"pekerja"
argument_list|,
literal|"kerja"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"tarikkan"
argument_list|,
literal|"tarik"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"ambilkan"
argument_list|,
literal|"ambil"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"mengambilkan"
argument_list|,
literal|"ambil"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"makanan"
argument_list|,
literal|"makan"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"janjian"
argument_list|,
literal|"janji"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"perjanjian"
argument_list|,
literal|"janji"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"tandai"
argument_list|,
literal|"tanda"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"dapati"
argument_list|,
literal|"dapat"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"mendapati"
argument_list|,
literal|"dapat"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"pantai"
argument_list|,
literal|"panta"
argument_list|)
expr_stmt|;
block|}
comment|/** Some detailed analysis examples (that might not be the best) */
DECL|method|testIRExamples
specifier|public
name|void
name|testIRExamples
parameter_list|()
throws|throws
name|IOException
block|{
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"penyalahgunaan"
argument_list|,
literal|"salahguna"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"menyalahgunakan"
argument_list|,
literal|"salahguna"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"disalahgunakan"
argument_list|,
literal|"salahguna"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"pertanggungjawaban"
argument_list|,
literal|"tanggungjawab"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"mempertanggungjawabkan"
argument_list|,
literal|"tanggungjawab"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"dipertanggungjawabkan"
argument_list|,
literal|"tanggungjawab"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"pelaksanaan"
argument_list|,
literal|"laksana"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"pelaksana"
argument_list|,
literal|"laksana"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"melaksanakan"
argument_list|,
literal|"laksana"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"dilaksanakan"
argument_list|,
literal|"laksana"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"melibatkan"
argument_list|,
literal|"libat"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"terlibat"
argument_list|,
literal|"libat"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"penculikan"
argument_list|,
literal|"culik"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"menculik"
argument_list|,
literal|"culik"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"diculik"
argument_list|,
literal|"culik"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"penculik"
argument_list|,
literal|"culik"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"perubahan"
argument_list|,
literal|"ubah"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"peledakan"
argument_list|,
literal|"ledak"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"penanganan"
argument_list|,
literal|"tangan"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"kepolisian"
argument_list|,
literal|"polisi"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"kenaikan"
argument_list|,
literal|"naik"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"bersenjata"
argument_list|,
literal|"senjata"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"penyelewengan"
argument_list|,
literal|"seleweng"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"kecelakaan"
argument_list|,
literal|"celaka"
argument_list|)
expr_stmt|;
block|}
comment|/* inflectional-only stemming */
DECL|field|b
name|Analyzer
name|b
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
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
name|tokenizer
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
name|tokenizer
argument_list|,
operator|new
name|IndonesianStemFilter
argument_list|(
name|tokenizer
argument_list|,
literal|false
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/** Test stemming only inflectional suffixes */
DECL|method|testInflectionalOnly
specifier|public
name|void
name|testInflectionalOnly
parameter_list|()
throws|throws
name|IOException
block|{
name|checkOneTerm
argument_list|(
name|b
argument_list|,
literal|"bukunya"
argument_list|,
literal|"buku"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|b
argument_list|,
literal|"bukukah"
argument_list|,
literal|"buku"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|b
argument_list|,
literal|"bukunyakah"
argument_list|,
literal|"buku"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|b
argument_list|,
literal|"dibukukannya"
argument_list|,
literal|"dibukukan"
argument_list|)
expr_stmt|;
block|}
DECL|method|testShouldntStem
specifier|public
name|void
name|testShouldntStem
parameter_list|()
throws|throws
name|IOException
block|{
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"bersenjata"
argument_list|,
literal|"senjata"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"bukukah"
argument_list|,
literal|"buku"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"gigi"
argument_list|,
literal|"gigi"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
