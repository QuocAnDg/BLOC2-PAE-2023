const getAvatars = async () => {

    const response = await fetch(`${process.env.API_BASE_URL}/photo/avatars`);
    if (!response.ok) throw new Error(`fetch error : ${response.status} : ${response.statusText}`);

    const avatars = await response.json();
    return avatars;
}

export default getAvatars;