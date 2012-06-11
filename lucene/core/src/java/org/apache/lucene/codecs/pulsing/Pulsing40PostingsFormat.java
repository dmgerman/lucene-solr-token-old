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
name|codecs
operator|.
name|BlockTreeTermsWriter
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
name|lucene40
operator|.
name|Lucene40PostingsBaseFormat
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
name|lucene40
operator|.
name|Lucene40PostingsFormat
import|;
end_import
begin_comment
comment|// javadocs
end_comment
begin_comment
comment|/**  * Concrete pulsing implementation over {@link Lucene40PostingsFormat}.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|Pulsing40PostingsFormat
specifier|public
class|class
name|Pulsing40PostingsFormat
extends|extends
name|PulsingPostingsFormat
block|{
comment|/** Inlines docFreq=1 terms, otherwise uses the normal "Lucene40" format. */
DECL|method|Pulsing40PostingsFormat
specifier|public
name|Pulsing40PostingsFormat
parameter_list|()
block|{
name|this
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/** Inlines docFreq=<code>freqCutoff</code> terms, otherwise uses the normal "Lucene40" format. */
DECL|method|Pulsing40PostingsFormat
specifier|public
name|Pulsing40PostingsFormat
parameter_list|(
name|int
name|freqCutoff
parameter_list|)
block|{
name|this
argument_list|(
name|freqCutoff
argument_list|,
name|BlockTreeTermsWriter
operator|.
name|DEFAULT_MIN_BLOCK_SIZE
argument_list|,
name|BlockTreeTermsWriter
operator|.
name|DEFAULT_MAX_BLOCK_SIZE
argument_list|)
expr_stmt|;
block|}
comment|/** Inlines docFreq=<code>freqCutoff</code> terms, otherwise uses the normal "Lucene40" format. */
DECL|method|Pulsing40PostingsFormat
specifier|public
name|Pulsing40PostingsFormat
parameter_list|(
name|int
name|freqCutoff
parameter_list|,
name|int
name|minBlockSize
parameter_list|,
name|int
name|maxBlockSize
parameter_list|)
block|{
name|super
argument_list|(
literal|"Pulsing40"
argument_list|,
operator|new
name|Lucene40PostingsBaseFormat
argument_list|()
argument_list|,
name|freqCutoff
argument_list|,
name|minBlockSize
argument_list|,
name|maxBlockSize
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
