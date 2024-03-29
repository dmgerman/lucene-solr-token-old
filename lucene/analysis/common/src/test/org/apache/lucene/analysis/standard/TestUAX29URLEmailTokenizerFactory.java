begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.standard
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|standard
package|;
end_package
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
name|java
operator|.
name|io
operator|.
name|StringReader
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
name|util
operator|.
name|BaseTokenStreamFactoryTestCase
import|;
end_import
begin_comment
comment|/**  * A few tests based on org.apache.lucene.analysis.TestUAX29URLEmailTokenizer  */
end_comment
begin_class
DECL|class|TestUAX29URLEmailTokenizerFactory
specifier|public
class|class
name|TestUAX29URLEmailTokenizerFactory
extends|extends
name|BaseTokenStreamFactoryTestCase
block|{
DECL|method|testUAX29URLEmailTokenizer
specifier|public
name|void
name|testUAX29URLEmailTokenizer
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"Wha\u0301t's this thing do?"
argument_list|)
decl_stmt|;
name|Tokenizer
name|stream
init|=
name|tokenizerFactory
argument_list|(
literal|"UAX29URLEmail"
argument_list|)
operator|.
name|create
argument_list|(
name|newAttributeFactory
argument_list|()
argument_list|)
decl_stmt|;
name|stream
operator|.
name|setReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Wha\u0301t's"
block|,
literal|"this"
block|,
literal|"thing"
block|,
literal|"do"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testArabic
specifier|public
name|void
name|testArabic
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"Ø§ÙÙÙÙÙ Ø§ÙÙØ«Ø§Ø¦ÙÙ Ø§ÙØ£ÙÙ Ø¹Ù ÙÙÙÙØ¨ÙØ¯ÙØ§ ÙØ³ÙÙ \"Ø§ÙØ­ÙÙÙØ© Ø¨Ø§ÙØ£Ø±ÙØ§Ù: ÙØµØ© ÙÙÙÙØ¨ÙØ¯ÙØ§\" (Ø¨Ø§ÙØ¥ÙØ¬ÙÙØ²ÙØ©: Truth in Numbers: The Wikipedia Story)Ø Ø³ÙØªÙ Ø¥Ø·ÙØ§ÙÙ ÙÙ 2008."
argument_list|)
decl_stmt|;
name|Tokenizer
name|stream
init|=
name|tokenizerFactory
argument_list|(
literal|"UAX29URLEmail"
argument_list|)
operator|.
name|create
argument_list|(
name|newAttributeFactory
argument_list|()
argument_list|)
decl_stmt|;
name|stream
operator|.
name|setReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø§ÙÙÙÙÙ"
block|,
literal|"Ø§ÙÙØ«Ø§Ø¦ÙÙ"
block|,
literal|"Ø§ÙØ£ÙÙ"
block|,
literal|"Ø¹Ù"
block|,
literal|"ÙÙÙÙØ¨ÙØ¯ÙØ§"
block|,
literal|"ÙØ³ÙÙ"
block|,
literal|"Ø§ÙØ­ÙÙÙØ©"
block|,
literal|"Ø¨Ø§ÙØ£Ø±ÙØ§Ù"
block|,
literal|"ÙØµØ©"
block|,
literal|"ÙÙÙÙØ¨ÙØ¯ÙØ§"
block|,
literal|"Ø¨Ø§ÙØ¥ÙØ¬ÙÙØ²ÙØ©"
block|,
literal|"Truth"
block|,
literal|"in"
block|,
literal|"Numbers"
block|,
literal|"The"
block|,
literal|"Wikipedia"
block|,
literal|"Story"
block|,
literal|"Ø³ÙØªÙ"
block|,
literal|"Ø¥Ø·ÙØ§ÙÙ"
block|,
literal|"ÙÙ"
block|,
literal|"2008"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testChinese
specifier|public
name|void
name|testChinese
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"ææ¯ä¸­å½äººã ï¼ï¼ï¼ï¼ ï¼´ï½ï½ï½ï½ "
argument_list|)
decl_stmt|;
name|Tokenizer
name|stream
init|=
name|tokenizerFactory
argument_list|(
literal|"UAX29URLEmail"
argument_list|)
operator|.
name|create
argument_list|(
name|newAttributeFactory
argument_list|()
argument_list|)
decl_stmt|;
name|stream
operator|.
name|setReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"æ"
block|,
literal|"æ¯"
block|,
literal|"ä¸­"
block|,
literal|"å½"
block|,
literal|"äºº"
block|,
literal|"ï¼ï¼ï¼ï¼"
block|,
literal|"ï¼´ï½ï½ï½ï½"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testKorean
specifier|public
name|void
name|testKorean
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"ìëíì¸ì íê¸ìëë¤"
argument_list|)
decl_stmt|;
name|Tokenizer
name|stream
init|=
name|tokenizerFactory
argument_list|(
literal|"UAX29URLEmail"
argument_list|)
operator|.
name|create
argument_list|(
name|newAttributeFactory
argument_list|()
argument_list|)
decl_stmt|;
name|stream
operator|.
name|setReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ìëíì¸ì"
block|,
literal|"íê¸ìëë¤"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testHyphen
specifier|public
name|void
name|testHyphen
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"some-dashed-phrase"
argument_list|)
decl_stmt|;
name|Tokenizer
name|stream
init|=
name|tokenizerFactory
argument_list|(
literal|"UAX29URLEmail"
argument_list|)
operator|.
name|create
argument_list|(
name|newAttributeFactory
argument_list|()
argument_list|)
decl_stmt|;
name|stream
operator|.
name|setReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"some"
block|,
literal|"dashed"
block|,
literal|"phrase"
block|}
argument_list|)
expr_stmt|;
block|}
comment|// Test with some URLs from TestUAX29URLEmailTokenizer's
comment|// urls.from.random.text.with.urls.txt
DECL|method|testURLs
specifier|public
name|void
name|testURLs
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|textWithURLs
init|=
literal|"http://johno.jsmf.net/knowhow/ngrams/index.php?table=en-dickens-word-2gram&paragraphs=50&length=200&no-ads=on\n"
operator|+
literal|" some extra\nWords thrown in here. "
operator|+
literal|"http://c5-3486.bisynxu.FR/aI.YnNms/"
operator|+
literal|" samba Halta gamba "
operator|+
literal|"ftp://119.220.152.185/JgJgdZ/31aW5c/viWlfQSTs5/1c8U5T/ih5rXx/YfUJ/xBW1uHrQo6.R\n"
operator|+
literal|"M19nq.0URV4A.Me.CC/mj0kgt6hue/dRXv8YVLOw9v/CIOqb\n"
operator|+
literal|"Https://yu7v33rbt.vC6U3.XN--KPRW13D/y%4fMSzkGFlm/wbDF4m"
operator|+
literal|" inter Locutio "
operator|+
literal|"[c2d4::]/%471j5l/j3KFN%AAAn/Fip-NisKH/\n"
operator|+
literal|"file:///aXvSZS34is/eIgM8s~U5dU4Ifd%c7"
operator|+
literal|" blah Sirrah woof "
operator|+
literal|"http://[a42:a7b6::]/qSmxSUU4z/%52qVl4\n"
decl_stmt|;
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
name|textWithURLs
argument_list|)
decl_stmt|;
name|Tokenizer
name|stream
init|=
name|tokenizerFactory
argument_list|(
literal|"UAX29URLEmail"
argument_list|)
operator|.
name|create
argument_list|(
name|newAttributeFactory
argument_list|()
argument_list|)
decl_stmt|;
name|stream
operator|.
name|setReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"http://johno.jsmf.net/knowhow/ngrams/index.php?table=en-dickens-word-2gram&paragraphs=50&length=200&no-ads=on"
block|,
literal|"some"
block|,
literal|"extra"
block|,
literal|"Words"
block|,
literal|"thrown"
block|,
literal|"in"
block|,
literal|"here"
block|,
literal|"http://c5-3486.bisynxu.FR/aI.YnNms/"
block|,
literal|"samba"
block|,
literal|"Halta"
block|,
literal|"gamba"
block|,
literal|"ftp://119.220.152.185/JgJgdZ/31aW5c/viWlfQSTs5/1c8U5T/ih5rXx/YfUJ/xBW1uHrQo6.R"
block|,
literal|"M19nq.0URV4A.Me.CC/mj0kgt6hue/dRXv8YVLOw9v/CIOqb"
block|,
literal|"Https://yu7v33rbt.vC6U3.XN--KPRW13D/y%4fMSzkGFlm/wbDF4m"
block|,
literal|"inter"
block|,
literal|"Locutio"
block|,
literal|"[c2d4::]/%471j5l/j3KFN%AAAn/Fip-NisKH/"
block|,
literal|"file:///aXvSZS34is/eIgM8s~U5dU4Ifd%c7"
block|,
literal|"blah"
block|,
literal|"Sirrah"
block|,
literal|"woof"
block|,
literal|"http://[a42:a7b6::]/qSmxSUU4z/%52qVl4"
block|}
argument_list|)
expr_stmt|;
block|}
comment|// Test with some emails from TestUAX29URLEmailTokenizer's
comment|// email.addresses.from.random.text.with.email.addresses.txt
DECL|method|testEmails
specifier|public
name|void
name|testEmails
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|textWithEmails
init|=
literal|" some extra\nWords thrown in here. "
operator|+
literal|"dJ8ngFi@avz13m.CC\n"
operator|+
literal|"kU-l6DS@[082.015.228.189]\n"
operator|+
literal|"\"%U\u0012@?\\B\"@Fl2d.md"
operator|+
literal|" samba Halta gamba "
operator|+
literal|"Bvd#@tupjv.sn\n"
operator|+
literal|"SBMm0Nm.oyk70.rMNdd8k.#ru3LI.gMMLBI.0dZRD4d.RVK2nY@au58t.B13albgy4u.mt\n"
operator|+
literal|"~+Kdz@3mousnl.SE\n"
operator|+
literal|" inter Locutio "
operator|+
literal|"C'ts`@Vh4zk.uoafcft-dr753x4odt04q.UY\n"
operator|+
literal|"}0tzWYDBuy@cSRQAABB9B.7c8xawf75-cyo.PM"
operator|+
literal|" blah Sirrah woof "
operator|+
literal|"lMahAA.j/5.RqUjS745.DtkcYdi@d2-4gb-l6.ae\n"
operator|+
literal|"lv'p@tqk.vj5s0tgl.0dlu7su3iyiaz.dqso.494.3hb76.XN--MGBAAM7A8H\n"
decl_stmt|;
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
name|textWithEmails
argument_list|)
decl_stmt|;
name|Tokenizer
name|stream
init|=
name|tokenizerFactory
argument_list|(
literal|"UAX29URLEmail"
argument_list|)
operator|.
name|create
argument_list|(
name|newAttributeFactory
argument_list|()
argument_list|)
decl_stmt|;
name|stream
operator|.
name|setReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"some"
block|,
literal|"extra"
block|,
literal|"Words"
block|,
literal|"thrown"
block|,
literal|"in"
block|,
literal|"here"
block|,
literal|"dJ8ngFi@avz13m.CC"
block|,
literal|"kU-l6DS@[082.015.228.189]"
block|,
literal|"\"%U\u0012@?\\B\"@Fl2d.md"
block|,
literal|"samba"
block|,
literal|"Halta"
block|,
literal|"gamba"
block|,
literal|"Bvd#@tupjv.sn"
block|,
literal|"SBMm0Nm.oyk70.rMNdd8k.#ru3LI.gMMLBI.0dZRD4d.RVK2nY@au58t.B13albgy4u.mt"
block|,
literal|"~+Kdz@3mousnl.SE"
block|,
literal|"inter"
block|,
literal|"Locutio"
block|,
literal|"C'ts`@Vh4zk.uoafcft-dr753x4odt04q.UY"
block|,
literal|"}0tzWYDBuy@cSRQAABB9B.7c8xawf75-cyo.PM"
block|,
literal|"blah"
block|,
literal|"Sirrah"
block|,
literal|"woof"
block|,
literal|"lMahAA.j/5.RqUjS745.DtkcYdi@d2-4gb-l6.ae"
block|,
literal|"lv'p@tqk.vj5s0tgl.0dlu7su3iyiaz.dqso.494.3hb76.XN--MGBAAM7A8H"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testMaxTokenLength
specifier|public
name|void
name|testMaxTokenLength
parameter_list|()
throws|throws
name|Exception
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
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
operator|++
name|i
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|"abcdefg"
argument_list|)
expr_stmt|;
comment|// 7 * 100 = 700 char "word"
block|}
name|String
name|longWord
init|=
name|builder
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|content
init|=
literal|"one two three "
operator|+
name|longWord
operator|+
literal|" four five six"
decl_stmt|;
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
name|content
argument_list|)
decl_stmt|;
name|Tokenizer
name|stream
init|=
name|tokenizerFactory
argument_list|(
literal|"UAX29URLEmail"
argument_list|,
literal|"maxTokenLength"
argument_list|,
literal|"1000"
argument_list|)
operator|.
name|create
argument_list|(
name|newAttributeFactory
argument_list|()
argument_list|)
decl_stmt|;
name|stream
operator|.
name|setReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"one"
block|,
literal|"two"
block|,
literal|"three"
block|,
name|longWord
block|,
literal|"four"
block|,
literal|"five"
block|,
literal|"six"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Test that bogus arguments result in exception */
DECL|method|testBogusArguments
specifier|public
name|void
name|testBogusArguments
parameter_list|()
throws|throws
name|Exception
block|{
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|tokenizerFactory
argument_list|(
literal|"UAX29URLEmail"
argument_list|,
literal|"bogusArg"
argument_list|,
literal|"bogusValue"
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Unknown parameters"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIllegalArguments
specifier|public
name|void
name|testIllegalArguments
parameter_list|()
throws|throws
name|Exception
block|{
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|tokenizerFactory
argument_list|(
literal|"UAX29URLEmail"
argument_list|,
literal|"maxTokenLength"
argument_list|,
literal|"-1"
argument_list|)
operator|.
name|create
argument_list|()
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"maxTokenLength must be greater than zero"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
