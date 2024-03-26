import { LeaderboardEntry } from "./LeaderboardEntry";
import { useEffect, useState } from "react";
import { ExperienceMember, GuildInfo } from "../data/leaderboard";
import { ExperienceConfigDisplay } from "./ExperienceConfigDisplay";
import { ErrorDisplay } from "./ErrorDisplay";

export function Leaderboard({ serverId }: { serverId: bigint }) {

    const pageSize = 25;

    const [members, setMembers] = useState<ExperienceMember[]>([])
    const [memberCount, setMemberCount] = useState(0)
    const [pageCount, setPageCount] = useState(0)
    const [hasMore, setHasMore] = useState(true)
    const [hasError, setError] = useState(false)
    const [guildInfo, setGuildInfo] = useState<GuildInfo>({} as GuildInfo)

    async function loadLeaderboard(page: number, size: number) {
        try {
            const leaderboardResponse = await fetch(`https://sissi-dev.sheldan.dev/experience/v1/leaderboards/${serverId}?page=${page}&size=${size}`)
            const leaderboardJson = await leaderboardResponse.json()
            const loadedMembers: Array<ExperienceMember> = leaderboardJson.content;
            setMemberCount(memberCount + loadedMembers.length)
            setHasMore(!leaderboardJson.last)
            setPageCount(page)
            setMembers(members.concat(loadedMembers))
        } catch (error) {
            console.log(error)
            setError(true)
        }
    }

    async function loadGuildInfo() {
        try {
            const guildInfoResponse = await fetch(`/servers/v1/${serverId}/info`)
            const guildInfoJson: GuildInfo = await guildInfoResponse.json()
            setGuildInfo(guildInfoJson)
        } catch (error) {
            console.log(error)
        }
    }

    useEffect(() => {
        if (memberCount === 0) {
            loadLeaderboard(0, pageSize)
        }
        loadGuildInfo()
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [])

    function loadMore() {
        loadLeaderboard(pageCount + 1, pageSize)
    }
    let loadMoreButton = <button className="w-full bg-gray-500 hover:bg-gray-700 text-white" onClick={loadMore}>Load more</button>;
    return (
        <>
            {!hasError ?
                <>
                    <div className="relative font-[sans-serif] before:absolute before:w-full before:h-full before:inset-0 before:bg-black before:opacity-50 before:z-10 h-48">
                        {guildInfo.bannerUrl !== null ? <img src={guildInfo.bannerUrl + "?size=4096"}
                            alt="Banner"
                            className="absolute inset-0 w-full h-full object-cover" /> : ''}
                        <div
                            className="min-h-36 relative z-50 h-full max-w-6xl mx-auto flex flex-row justify-center items-center text-center text-white p-6">
                            {guildInfo.iconUrl !== null ? <img
                                src={"https://cdn.discordapp.com/icons/297910194841583616/14226616a2d7b9be0de271bb58c28ae0.webp?size=96"}
                                alt="Icon"
                                className="lg:w-24 w-20 aspect-square object-cover absolute -bottom-10 rounded-xl border-4 border-slate-700" />
                                : ''}
                            <h1 className="text-4xl font-extrabold leading-none tracking-tight md:text-5xl lg:text-6xl text-white">{'Leaderboard for ' + guildInfo.name}</h1>
                        </div>
                    </div>
                    <div className="flex flex-col-reverse lg:flex-row w-full lg:w-11/12 mx-auto mt-12">
                        <div className="text-sm text-left lg:w-3/4 w-11/12 mx-auto mt-8 lg:p-8 p-4 rounded-xl bg-slate-900">
                            <h2 className="text-xl lg:text-3xl font-extrabold leading-none tracking-tight text-gray-100 p-4 pt-0">Leaderboard</h2>
                            <div className="flex p-4 rounded-xl text-slate-200 font-semibold text-center text-xs lg:text-base">
                                <p className="w-1/6">Rank</p>
                                <p className="w-3/6 text-start">Member</p>
                                <p className="w-1/5">Experience</p>
                                <p className="w-1/5">Messages</p>
                                <p className="w-1/5">Level</p>
                            </div>
                            <div className="flex flex-col gap-2">
                                {members.map(member => <LeaderboardEntry key={member.id} member={member} />)}
                            </div>
                            {hasMore ? loadMoreButton : ''}
                        </div>
                        <div className="lg:w-1/4 mt-8">
                            <ExperienceConfigDisplay serverId={serverId} />
                        </div>
                    </div>
                </>
                : <ErrorDisplay />}
        </>
    );
}

