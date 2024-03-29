begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.util.hll
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|hll
package|;
end_package
begin_comment
comment|/**  * Reads 'words' of a fixed width, in sequence, from a byte array.  */
end_comment
begin_interface
DECL|interface|IWordDeserializer
specifier|public
interface|interface
name|IWordDeserializer
block|{
comment|/**      * @return the next word in the sequence. Should not be called more than      * {@link #totalWordCount()} times.      */
DECL|method|readWord
name|long
name|readWord
parameter_list|()
function_decl|;
comment|/**      * Returns the number of words that could be encoded in the sequence.      *      * NOTE:  the sequence that was encoded may be shorter than the value this      *        method returns due to padding issues within bytes. This guarantees      *        only an upper bound on the number of times {@link #readWord()}      *        can be called.      *      * @return the maximum number of words that could be read from the sequence.      */
DECL|method|totalWordCount
name|int
name|totalWordCount
parameter_list|()
function_decl|;
block|}
end_interface
end_unit
