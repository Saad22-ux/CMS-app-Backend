<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/">
        <html>
            <body>
                <h1>Professors</h1>

                <xsl:for-each select="professors/professor">
                    <div style="border:1px solid gray; padding:10px; margin:10px;">
                        <h2><xsl:value-of select="name"/></h2>
                        <p><xsl:value-of select="bio"/></p>

                        <h4>Skills</h4>
                        <ul>
                            <xsl:for-each select="skills/skill">
                                <li><xsl:value-of select="."/></li>
                            </xsl:for-each>
                        </ul>

                        <h4>Publications</h4>
                        <ul>
                            <xsl:for-each select="publications/publication">
                                <li>
                                    <xsl:value-of select="."/> -
                                    <xsl:value-of select="@year"/>
                                </li>
                            </xsl:for-each>
                        </ul>
                    </div>
                </xsl:for-each>

            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
