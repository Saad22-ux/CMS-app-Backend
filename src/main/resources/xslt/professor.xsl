<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/">
        <html>
            <head>
                <title>Professor Profile</title>
            </head>
            <body>

                <h1>Professor Profile</h1>

                <h2>Biography</h2>
                <p>
                    <xsl:value-of select="/professor/bio"/>
                </p>

                <h2>Skills</h2>
                <ul>
                    <xsl:for-each select="/professor/skills/skill">
                        <li><xsl:value-of select="."/></li>
                    </xsl:for-each>
                </ul>

                <h2>Publications</h2>
                <ul>
                    <xsl:for-each select="/professor/publications/publication">
                        <li>
                            <xsl:value-of select="."/>
                            (<xsl:value-of select="@year"/>)
                        </li>
                    </xsl:for-each>
                </ul>

            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>
