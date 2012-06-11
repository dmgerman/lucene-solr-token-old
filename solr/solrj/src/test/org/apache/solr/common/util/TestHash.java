begin_unit
begin_package
DECL|package|org.apache.solr.common.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import
begin_comment
comment|/** Tests for lookup3ycs hash functions  */
end_comment
begin_class
DECL|class|TestHash
specifier|public
class|class
name|TestHash
extends|extends
name|LuceneTestCase
block|{
comment|// Test that the java version produces the same output as the C version
DECL|method|testEqualsLOOKUP3
specifier|public
name|void
name|testEqualsLOOKUP3
parameter_list|()
block|{
name|int
index|[]
name|hashes
init|=
operator|new
name|int
index|[]
block|{
literal|0xc4c20dd5
block|,
literal|0x3ab04cc3
block|,
literal|0xebe874a3
block|,
literal|0x0e770ef3
block|,
literal|0xec321498
block|,
literal|0x73845e86
block|,
literal|0x8a2db728
block|,
literal|0x03c313bb
block|,
literal|0xfe5b9199
block|,
literal|0x95965125
block|,
literal|0xcbc4e7c2
block|}
decl_stmt|;
comment|/*** the hash values were generated by adding the following to lookup3.c      *      * char* s = "hello world";      * int len = strlen(s);      * uint32_t a[len];      * for (int i=0; i<len; i++) {      *   a[i]=s[i];      *   uint32_t result = hashword(a, i+1, i*12345);      *   printf("0x%.8x\n", result);      * }      *      */
name|String
name|s
init|=
literal|"hello world"
decl_stmt|;
name|int
index|[]
name|a
init|=
operator|new
name|int
index|[
name|s
operator|.
name|length
argument_list|()
index|]
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
name|s
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|a
index|[
name|i
index|]
operator|=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|int
name|len
init|=
name|i
operator|+
literal|1
decl_stmt|;
name|int
name|hash
init|=
name|Hash
operator|.
name|lookup3
argument_list|(
name|a
argument_list|,
literal|0
argument_list|,
name|len
argument_list|,
name|i
operator|*
literal|12345
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|hashes
index|[
name|i
index|]
argument_list|,
name|hash
argument_list|)
expr_stmt|;
name|int
name|hash2
init|=
name|Hash
operator|.
name|lookup3ycs
argument_list|(
name|a
argument_list|,
literal|0
argument_list|,
name|len
argument_list|,
name|i
operator|*
literal|12345
operator|+
operator|(
name|len
operator|<<
literal|2
operator|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|hashes
index|[
name|i
index|]
argument_list|,
name|hash2
argument_list|)
expr_stmt|;
name|int
name|hash3
init|=
name|Hash
operator|.
name|lookup3ycs
argument_list|(
name|s
argument_list|,
literal|0
argument_list|,
name|len
argument_list|,
name|i
operator|*
literal|12345
operator|+
operator|(
name|len
operator|<<
literal|2
operator|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|hashes
index|[
name|i
index|]
argument_list|,
name|hash3
argument_list|)
expr_stmt|;
block|}
block|}
comment|// test that the hash of the UTF-16 encoded Java String is equal to the hash of the unicode code points
DECL|method|tstEquiv
name|void
name|tstEquiv
parameter_list|(
name|int
index|[]
name|utf32
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|int
name|seed
init|=
literal|100
decl_stmt|;
name|StringBuilder
name|sb
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
name|len
condition|;
name|i
operator|++
control|)
name|sb
operator|.
name|appendCodePoint
argument_list|(
name|utf32
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|int
name|hash
init|=
name|Hash
operator|.
name|lookup3
argument_list|(
name|utf32
argument_list|,
literal|0
argument_list|,
name|len
argument_list|,
name|seed
operator|-
operator|(
name|len
operator|<<
literal|2
operator|)
argument_list|)
decl_stmt|;
name|int
name|hash2
init|=
name|Hash
operator|.
name|lookup3ycs
argument_list|(
name|utf32
argument_list|,
literal|0
argument_list|,
name|len
argument_list|,
name|seed
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|hash
argument_list|,
name|hash2
argument_list|)
expr_stmt|;
name|int
name|hash3
init|=
name|Hash
operator|.
name|lookup3ycs
argument_list|(
name|sb
argument_list|,
literal|0
argument_list|,
name|sb
operator|.
name|length
argument_list|()
argument_list|,
name|seed
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|hash
argument_list|,
name|hash3
argument_list|)
expr_stmt|;
name|long
name|hash4
init|=
name|Hash
operator|.
name|lookup3ycs64
argument_list|(
name|sb
argument_list|,
literal|0
argument_list|,
name|sb
operator|.
name|length
argument_list|()
argument_list|,
name|seed
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|(
name|int
operator|)
name|hash4
argument_list|,
name|hash
argument_list|)
expr_stmt|;
block|}
DECL|method|testHash
specifier|public
name|void
name|testHash
parameter_list|()
block|{
name|Random
name|r
init|=
name|random
argument_list|()
decl_stmt|;
name|int
index|[]
name|utf32
init|=
operator|new
name|int
index|[
literal|20
index|]
decl_stmt|;
name|tstEquiv
argument_list|(
name|utf32
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|utf32
index|[
literal|0
index|]
operator|=
literal|0x10000
expr_stmt|;
name|tstEquiv
argument_list|(
name|utf32
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|utf32
index|[
literal|0
index|]
operator|=
literal|0x8000
expr_stmt|;
name|tstEquiv
argument_list|(
name|utf32
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|utf32
index|[
literal|0
index|]
operator|=
name|Character
operator|.
name|MAX_CODE_POINT
expr_stmt|;
name|tstEquiv
argument_list|(
name|utf32
argument_list|,
literal|1
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|10000
condition|;
name|iter
operator|++
control|)
block|{
name|int
name|len
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|utf32
operator|.
name|length
operator|+
literal|1
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
name|len
condition|;
name|i
operator|++
control|)
block|{
name|int
name|codePoint
decl_stmt|;
do|do
block|{
name|codePoint
operator|=
name|r
operator|.
name|nextInt
argument_list|(
name|Character
operator|.
name|MAX_CODE_POINT
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
operator|(
name|codePoint
operator|&
literal|0xF800
operator|)
operator|==
literal|0xD800
condition|)
do|;
comment|// avoid surrogate code points
name|utf32
index|[
name|i
index|]
operator|=
name|codePoint
expr_stmt|;
block|}
comment|// System.out.println("len="+len + ","+utf32[0]+","+utf32[1]);
name|tstEquiv
argument_list|(
name|utf32
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
