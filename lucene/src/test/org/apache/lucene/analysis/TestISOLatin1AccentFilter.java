begin_unit
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|CharTermAttribute
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
begin_class
DECL|class|TestISOLatin1AccentFilter
specifier|public
class|class
name|TestISOLatin1AccentFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testU
specifier|public
name|void
name|testU
parameter_list|()
throws|throws
name|Exception
block|{
name|TokenStream
name|stream
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"Des mot clÃ©s Ã LA CHAÃNE Ã Ã Ã Ã Ã Ã Ã Ã Ã Ã Ã Ã Ã Ã Ã Ã Ä² Ã Ã Ã Ã Ã Ã Ã Ã Å Ã Ã Ã Ã Ã Ã Å¸ Ã  Ã¡ Ã¢ Ã£ Ã¤ Ã¥ Ã¦ Ã§ Ã¨ Ã© Ãª Ã« Ã¬ Ã­ Ã® Ã¯ Ä³ Ã° Ã± Ã² Ã³ Ã´ Ãµ Ã¶ Ã¸ Å Ã Ã¾ Ã¹ Ãº Ã» Ã¼ Ã½ Ã¿ ï¬ ï¬"
argument_list|)
argument_list|)
decl_stmt|;
name|ISOLatin1AccentFilter
name|filter
init|=
operator|new
name|ISOLatin1AccentFilter
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|CharTermAttribute
name|termAtt
init|=
name|filter
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTermEquals
argument_list|(
literal|"Des"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"mot"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"cles"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"A"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"LA"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"CHAINE"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"A"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"A"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"A"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"A"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"A"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"A"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"AE"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"C"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"E"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"E"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"E"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"E"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"I"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"I"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"I"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"I"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"IJ"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"D"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"N"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"O"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"O"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"O"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"O"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"O"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"O"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"OE"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"TH"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"U"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"U"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"U"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"U"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"Y"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"Y"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"a"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"a"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"a"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"a"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"a"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"a"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"ae"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"c"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"e"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"e"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"e"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"e"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"i"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"i"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"i"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"i"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"ij"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"d"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"n"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"o"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"o"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"o"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"o"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"o"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"o"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"oe"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"ss"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"th"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"u"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"u"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"u"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"u"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"y"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"y"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"fi"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"fl"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|filter
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertTermEquals
name|void
name|assertTermEquals
parameter_list|(
name|String
name|expected
parameter_list|,
name|TokenStream
name|stream
parameter_list|,
name|CharTermAttribute
name|termAtt
parameter_list|)
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|stream
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
